FROM ubuntu:latest
LABEL authors="jianzhang"

FROM nvidia/cuda:12.6.3-base-ubuntu22.04

COPY source.list /etc/apt/sources.list
RUN apt update && apt install -y software-properties-common && add-apt-repository ppa:deadsnakes/ppa && apt update && DEBIAN_FRONTEND=noninteractive apt install -y gnutls-bin git-all python3.11 python3-pip && git config --global http.version HTTP/1.1;git config --global http.sslVerify false;git config --global http.postBuffer 1048576000;pip3 config set global.extra-index-url "https://mirrors.tuna.tsinghua.edu.cn/pypi/web/simple"
RUN cd /home && git clone https://gitclone.com/github.com/comfyanonymous/ComfyUI.git
RUN cd /home/ComfyUI && pip3 install xformers!=0.0.18 insightface diffusers ultralytics -r requirements.txt
RUN cd /home/ComfyUI/custom_nodes && git clone https://github.com/ltdrdata/ComfyUI-Manager && cd /home/ComfyUI/custom_nodes/ComfyUI-Manager && pip3 install -r requirements.txt

WORKDIR /home/ComfyUI/

VOLUME /home/ComfyUI/models/
VOLUME /home/ComfyUI/custom_nodes/

EXPOSE 8188
ENTRYPOINT ["python","main.py"]
HEALTHCHECK --interval=8s --timeout=5s CMD curl -fs http://localhost:8188/ || exit 1