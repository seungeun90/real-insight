# 1. Nginx 공식 이미지 사용
FROM nginx:alpine

# 2. 루트 사용자로 실행 (선택 사항)
USER root

# 3. 현재 디렉토리의 파일을 Nginx의 HTML 디렉토리로 복사
COPY . /usr/share/nginx/html

# 4. Nginx 실행
CMD ["nginx", "-g", "daemon off;"]
