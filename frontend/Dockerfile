# ==========================
# Étape 1 : Build Angular
# ==========================
FROM node:20 AS build

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

# ==========================
# Étape 2 : Nginx
# ==========================
FROM nginx:alpine

COPY --from=build /app/dist/student-management-frontend/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]