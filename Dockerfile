FROM ubuntu

RUN apt-get update -qq -y && apt-get upgrade -qq -y
RUN apt-get install g++ make libcppunit-dev -qq -y


ADD . /code
WORKDIR /code
RUN make all -s

CMD bin/servo --config=files/examples/servod-dummy.conf

EXPOSE 2047