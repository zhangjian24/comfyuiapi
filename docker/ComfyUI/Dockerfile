FROM pytorch/pytorch:2.6.0-cuda12.6-cudnn9-devel
LABEL authors="jianzhang"

COPY source.list /etc/apt/sources.list
RUN apt update && apt install -y software-properties-common && add-apt-repository ppa:deadsnakes/ppa && apt update && DEBIAN_FRONTEND=noninteractive apt install -y build-essential libcurl4-openssl-dev gnutls-bin git-all
RUN git config --global http.version HTTP/1.1;git config --global http.sslVerify false;git config --global http.postBuffer 1048576000
RUN cd /workspace && git clone git://github.com/comfyanonymous/ComfyUI.git
#COPY ComfyUI /workspace/ComfyUI
RUN cd /workspace/ComfyUI && pip install xformers!=0.0.18 insightface diffusers ultralytics -r requirements.txt
RUN cd /workspace/ComfyUI/custom_nodes && git clone git://github.com/ltdrdata/ComfyUI-Manager.git
#COPY ComfyUI-Manager /workspace/ComfyUI/custom_nodes/ComfyUI-Manager
RUN cd /workspace/ComfyUI/custom_nodes/ComfyUI-Manager && pip install -r requirements.txt

WORKDIR /workspace/ComfyUI/

VOLUME /workspace/ComfyUI/models/
VOLUME /workspace/ComfyUI/custom_nodes/

EXPOSE 8188
ENTRYPOINT ["python","main.py"]
HEALTHCHECK --interval=8s --timeout=5s CMD curl -fs http://localhost:8188/ || exit 1