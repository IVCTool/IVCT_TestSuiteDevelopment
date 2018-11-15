FROM busybox
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

COPY ./build/distributions/TS_HelloWorld-1.0.1 /root/conf/TestSuites/TS_HelloWorld-1.0.1
ENV VOLUMEDIR /root/conf/TestSuites/TS_HelloWorld-1.0.1
ENV VOLUME ${VOLUMEDIR}/.
VOLUME ${VOLUMEDIR}