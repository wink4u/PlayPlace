# infra/Dockerfile

FROM node:18-alpine AS base

FROM base AS deps
# alpine 이미지는 glibc 대신 musl libc을 사용.
# 특정 라이브러리에 대해 문제가 발생할 수 있으므로 libc6-compat 패키지를 추가하는 것이 좋다.
# https://github.com/nodejs/docker-node/tree/main#nodealpine

RUN apk add --no-cache libc6-compat

WORKDIR /app

# 의존성 패키지 설치
COPY package.json package-lock.json ./

RUN npm ci --force

FROM base AS builder

WORKDIR /app

ARG NEXT_PUBLIC_SERVER_URL
ARG NEXT_PUBLIC_DEVELOP_URL
ARG NEXT_PUBLIC_GOOGLE_LOGIN_PATH
ARG NEXT_PUBLIC_DEVELOP_GOOGLE_LOGIN_PATH
ARG NEXT_PUBLIC_GOOGLE_MAPS
ARG NEXT_PUBLIC_WS_BASE_URL
ARG NEXT_PUBLIC_DEVELOP_WS_BASE_URL

COPY . .

# deps 단계에서 설치한 의존성 패키지 복사
COPY --from=deps /app/node_modules ./node_modules

# build 진행
RUN npm run build

FROM base AS runner


WORKDIR /app


# 보안 문제가 발생할 수 있으므로 도커 컨테이너 내에서 루트 권한으로 서버 프로세스를 실행하지 않는 것이 좋다.
RUN addgroup --system --gid 1001 nodejs

RUN adduser --system --uid 1001 nextjs

# standalone 폴더 및 정적 파일 복사
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./

COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

COPY --from=builder --chown=nextjs:nodejs /app/public ./public

USER nextjs

EXPOSE 3000

ENV PORT 3000

CMD ["node", "server.js"]