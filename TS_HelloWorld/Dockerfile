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

ADD ./build/distributions/TS_HelloWorld-2.0.0.tar /root/conf/TestSuites/
ENV VOLUMEDIR /root/conf/TestSuites/TS_HelloWorld-2.0.0
ENV VOLUME ${VOLUMEDIR}/.
VOLUME ${VOLUMEDIR}
