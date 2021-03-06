FROM openjdk:8-jre-slim as base
RUN apt-get update && apt-get install -y \
    build-essential \
    wget \
    python-pip \
    python3 \
    python3-pip \
    liblzma-dev \
    libbz2-dev \
    zlib1g-dev \
    python \
    git \
    libssl-dev \
    libpng-dev \
    python-dev

RUN mkdir /software
WORKDIR /software
ENV PATH="/software:${PATH}"

RUN wget https://hgdownload.cse.ucsc.edu/admin/exe/linux.x86_64/bedGraphToBigWig -O /bin/bedGraphToBigWig && \
    chmod +x /bin/bedGraphToBigWig

RUN wget https://github.com/alexdobin/STAR/archive/2.5.1b.tar.gz && tar -xzf 2.5.1b.tar.gz && \
    cd STAR-2.5.1b && make STAR && rm ../2.5.1b.tar.gz


ENV PATH="/software/STAR-2.5.1b/bin/Linux_x86_64:${PATH}"


FROM openjdk:8-jdk-alpine as build
COPY . /src
WORKDIR /src

RUN ./gradlew clean shadowJar

FROM base
RUN mkdir /app
COPY --from=build /src/build/*.jar /app/bamtosignal.jar
