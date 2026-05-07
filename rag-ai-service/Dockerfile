# 生产环境镜像，同样改用 uv 安装锁定依赖
FROM python:3.11-slim

WORKDIR /app

# 系统依赖
RUN apt-get update && apt-get install -y \
  build-essential \
  default-libmysqlclient-dev \
  pkg-config \
  netcat-traditional \
  curl \
  && rm -rf /var/lib/apt/lists/*

# 拷贝 uv 二进制
COPY --from=ghcr.io/astral-sh/uv:0.4.30 /uv /usr/local/bin/uv

ENV UV_SYSTEM_PYTHON=1
ENV UV_COMPILE_BYTECODE=1
ENV UV_LINK_MODE=copy

# 先装依赖（利用 docker 缓存）
COPY pyproject.toml uv.lock ./
RUN uv sync --frozen --no-install-project --no-dev

# 拷贝应用代码
COPY entrypoint.sh ./
RUN chmod +x entrypoint.sh
COPY . .

RUN mkdir -p uploads

ENV PYTHONPATH=/app
ENV PATH="/app/.venv/bin:${PATH}"
ENV ENVIRONMENT=production

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
