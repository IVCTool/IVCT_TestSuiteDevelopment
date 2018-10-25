FROM alpine:3.3
ADD ./build/distributions/TS_HelloWorld-1.0.1.tar /root/conf/TestSuites/
ENTRYPOINT [ "sh", "-c", "if [ -n \"$1\" ]; then cp -r /root/conf/TestSuites/* $1; echo \"Copied data to $1\"; else /bin/true; fi", "--" ]