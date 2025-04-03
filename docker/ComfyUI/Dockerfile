FROM pytorch/pytorch:2.6.0-cuda12.4-cudnn9-runtime
LABEL authors="jianzhang"

COPY source.list /etc/apt/sources.list
RUN apt update && apt install -y software-properties-common && add-apt-repository ppa:deadsnakes/ppa && apt update && DEBIAN_FRONTEND=noninteractive apt install -y build-essential libcurl4-openssl-dev gnutls-bin git-all wget curl
RUN git config --global http.version HTTP/1.1;git config --global http.sslVerify false;git config --global http.postBuffer 1048576000
#RUN cd /workspace && git clone git://github.com/comfyanonymous/ComfyUI.git
COPY ComfyUI /workspace/ComfyUI
RUN cd /workspace/ComfyUI && pip install xformers insightface diffusers ultralytics -r requirements.txt -i http://mirrors.aliyun.com/pypi/simple/ --extra-index-url https://download.pytorch.org/whl/cu124 --extra-index-url https://pypi.python.org/simple --trusted-host mirrors.aliyun.com
#RUN cd /workspace/ComfyUI/custom_nodes && git clone git://github.com/ltdrdata/ComfyUI-Manager.git
COPY ComfyUI-Manager /workspace/ComfyUI/custom_nodes/ComfyUI-Manager
RUN cd /workspace/ComfyUI/custom_nodes/ComfyUI-Manager && pip install -r requirements.txt -i http://mirrors.aliyun.com/pypi/simple/ --extra-index-url https://pypi.python.org/simple --trusted-host mirrors.aliyun.com

VOLUME /workspace/ComfyUI/models/
VOLUME /workspace/ComfyUI/custom_nodes/

EXPOSE 8188
ENTRYPOINT ["python","/workspace/ComfyUI/main.py"]
HEALTHCHECK --interval=8s --timeout=5s CMD curl -fs http://localhost:8188/ || exit 1