"""
/internal/** 路由空间：仅由同集群内的其他服务（rag-document-service 等）通过
Nacos 服务发现直连调用。不挂到网关 rag-gateway 的 route 列表下，外部网络打不进来。

目前只有 US-010 添加的 embed_and_index 一个端点：Java 文档处理服务完成 MySQL
chunk 写入后，把 chunk 内容发到这里，由 Python 用 EmbeddingsFactory 统一做
向量化 + 写 ChromaDB，保证 embedding provider+model 只在 rag-ai-service 一份。
"""
from fastapi import APIRouter

from app.api.internal import embed

router = APIRouter()
router.include_router(embed.router, tags=["internal"])
