#edu-file

FROM java8_curl_lb

COPY edu-file.jar start.sh /home/

RUN chmod 777 /home/start.sh

WORKDIR /home

EXPOSE 9002 2000

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

CMD ["/bin/bash", "./start.sh", "$CFGURL"]
