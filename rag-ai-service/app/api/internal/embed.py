from typing import Any, Dict, List, Optional

from fastapi import APIRouter, HTTPException
from langchain_core.documents import Document
from pydantic import BaseModel, Field

from app.core.config import settings
from app.services.embedding.embedding_factory import EmbeddingsFactory
from app.services.vector_store import VectorStoreFactory

router = APIRouter()


class EmbedChunkItem(BaseModel):
    """由 rag-document-service 在 MySQL 里已经落库的单个 chunk。"""

    id: Optional[str] = Field(
        default=None,
        description="Chunk 的业务 id（通常等于 document_chunks.id 的 UUID），"
                    "写进向量库的 metadata 里方便回溯",
    )
    content: str = Field(..., description="chunk 的文本内容，即 page_content")
    metadata: Dict[str, Any] = Field(default_factory=dict)


class EmbedAndIndexRequest(BaseModel):
    kb_id: int
    chunks: List[EmbedChunkItem]


class EmbedAndIndexResponse(BaseModel):
    indexed: int
    collection: str


@router.post("/embed-and-index", response_model=EmbedAndIndexResponse)
def embed_and_index(request: EmbedAndIndexRequest) -> EmbedAndIndexResponse:
    """
    US-010 Option B 的 Python 端：Java 在 MySQL 里写完 chunk 后，把文本 POST
    过来，由 EmbeddingsFactory 统一做 embedding，再写进 ChromaDB 的
    `kb_{kb_id}` collection（与 /knowledge-base/test-retrieval 读取的 collection
    同名，保证写-读对称）。

    单点信任：provider + model 只在 rag-ai-service 的 settings 里配置一份，
    Java 不再自己调 OpenAI / Spring-AI。
    """
    if not request.chunks:
        return EmbedAndIndexResponse(indexed=0, collection=f"kb_{request.kb_id}")

    try:
        embeddings = EmbeddingsFactory.create()
        vector_store = VectorStoreFactory.create(
            store_type=settings.VECTOR_STORE_TYPE,
            collection_name=f"kb_{request.kb_id}",
            embedding_function=embeddings,
        )

        docs = []
        for item in request.chunks:
            metadata = dict(item.metadata) if item.metadata else {}
            if item.id is not None:
                # 写进 metadata 方便后续按 chunk id 追溯；不要覆盖调用方已有的 id
                metadata.setdefault("chunk_id", item.id)
            docs.append(Document(page_content=item.content, metadata=metadata))

        vector_store.add_documents(docs)

        return EmbedAndIndexResponse(
            indexed=len(docs),
            collection=f"kb_{request.kb_id}",
        )
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Failed to embed/index kb_{request.kb_id}: {e}",
        )
