 # author: Lucas Immanuel Nickel
 # matriculation: 1318441
 # date: 2023-11-19


FROM nginx:latest

RUN chown -R nginx:nginx /usr/share/nginx/html

RUN mkdir -p /var/cache/nginx/client_temp && \
    mkdir -p /var/run/nginx && \
    chown -R nginx:nginx /var/cache/nginx /var/run/nginx

WORKDIR /usr/share/nginx/html

COPY index.html .

COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
