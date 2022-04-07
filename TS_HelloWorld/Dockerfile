FROM busybox:latest
ENTRYPOINT [ "sh", "-c", " \
   if [ -n \"$VOLUME\" -a -n \"$1\" ]; then \
      if [ -n \"$OPTS\" ]; then \
         cp $OPTS $VOLUME $1; \
      else \
         cp -a $VOLUME $1; \
      fi; \
      echo \"Copied $VOLUME to $1\"; \
   fi" , \
"--" ]

ARG version

ADD ./build/distributions/TS_HelloWorld-${version}.tar /root/conf/TestSuites/
ENV VOLUMEDIR /root/conf/TestSuites/TS_HelloWorld-${version}
ENV VOLUME ${VOLUMEDIR}/.
VOLUME ${VOLUMEDIR}
