#! /bin/bash
#
#
# tomcat-startscript.sh 
#
# Script um tomcat mit den entsprechenden Umgebungsvariablen zu starten, stoppen#
# 18.10.2011 S.Barfuss, Fraunhofer IOSB
# Fraunhofer Institut für Optronik,Systemtechnik und Bildauswertung
#
#
# 20200319 brf Anpassungen auch an Version 8.5.51
# 20200127 brf Informationen ueber Herkunft der webapps zugefuegt
# 20191218 brf Angepasst fure Start des apache-tomcat-8.5.40 in IVCT_Runtime
# 20190327 brf Angepasst fure Start des apachetomcat7078 in IVCT_Runtime 

# die Webapplikationen wurden nach webapps kopiert von 
# /opt/IVCTool/IVCT_Framework/GUI/nato.ivct.gui.server.app.war/build/libs
# ivct.gui.server##2.2.2-SNAPSHOT.war
# und
# /opt/IVCTool/IVCT_Framework/GUI/nato.ivct.gui.ui.html.app.war/build/libs/
# ivct.gui.ui.html##2.2.2-SNAPSHOT.war


#----------------------------------------------------------------
#  allgemeine Variablen
#----------------------------------------------------------------
#set -x

#export LC_ALL="de_DE.UTF-8"

#export IVCTTOOL_Dir=/home1_phys/IVCTool_test
export IVCTTOOL_Dir=/opt/IVCTool_c
#export IVCTTOOL_Dir=/opt/IVCTool

export RUNTIME_Dir=${IVCTTOOL_Dir}/IVCT_Runtime


#----------------------------------------------------------------
#  java Variablen
#----------------------------------------------------------------


export JAVA_HOME=/opt/jdk1.8.0_161

#export JRE_HOME=$JAVA_HOME
#export JAVA_BIN=$JAVA_HOME/bin
#export CLASSPATH=$CLASSPATH:$JAVA_HOME:$JAVA_HOME/lib
#export PATH=$JAVA_BIN:$PATH

export JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
#export JAVA_OPTS=""
#export JAVA_OPTS="$JAVA_OPTS -Xnoclassgc"
#export JAVA_OPTS="$JAVA_OPTS -Xmx1200m"
#export JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=ISO-8859-1"
#export JAVA_OPTS="$JAVA_OPTS -Duser.language=de"
#export JAVA_OPTS="$JAVA_OPTS -Duser.country=DE"

# jconsole&        # debugg

 
#----------------------------------------------------------------
#  Tomcat Variablen
#----------------------------------------------------------------

# Tomcat Environment
#export CATALINA_HOME=/opt/apache-tomcat-8.5.24
export CATALINA_HOME=${RUNTIME_Dir}/apache-tomcat-8.5.51

#export CATALINA_BASE=/home/brf/anwendungsscripte_brf/tomweb-base_brf
export CATALINA_BASE=${CATALINA_HOME}

#export CATALINA_OPTS=""
#export CATALINA_OPTS="$CATALINA_OPTS -Djava.net.preferIPv4Stack=true"

### Fuer Verwendung von jconsole oder jvisualvm
#export CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote.port=1138 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"


#----------------------------------------------------------------
#  Tomcat Verzeichnisse und Berechtigungen
#----------------------------------------------------------------

if [ ! -d "${CATALINA_BASE}/logs" ] ; then
   mkdir ${CATALINA_BASE}/logs
fi 


chmod 744 ${CATALINA_HOME}/bin/startup.sh
chmod 744 ${CATALINA_HOME}/bin/shutdown.sh
chmod 744 ${CATALINA_HOME}/bin/catalina.sh

#----------------------------------------------------------------
#  IVCT  Umgebung
#----------------------------------------------------------------

export IVCT_HOME=${RUNTIME_Dir}
export IVCT_CONF=${RUNTIME_Dir}

#----------------------------------------------------------------
#  Startaufrufe
#----------------------------------------------------------------

case $1 in
 start)
   echo "start wurde eingegeben"
   #${CATALINA_HOME}/bin/catalina.sh start
   ${CATALINA_HOME}/bin/startup.sh
;;

 stop)
  echo "stop wurde eingegeben"
  ${CATALINA_HOME}/bin/shutdown.sh

 # ---- testen ob noch Java-Prozesse in dieser Umgebung laufen
 while ps -ef | grep IVCTool| grep apache-tomcat | grep java| grep -v grep ; do
  echo "teste ob tomcat dieser Umgebung noch laeuft, Warten, stoppen"
  echo "$linie" #-----------------------------------------
  count=${count:=0}
  echo $((++count))

  echo -e " Es laeuft weiterhin mind. 1 Java Prozess in dieser Umbebung:
  \n $(ps -ef | grep IVCTool| grep apache-tomcat | grep java| grep -v grep )
  \n Warte auf das Ende des Prozesses"
   sleep 3

  if [ $count -gt 2 ] ; then
    echo "Der Java-Prozess beendet sich offenbar nicht, stoppe ihn hart."
    ProzessId=$(ps -ef | grep IVCTool| grep apache-tomcat | grep java| grep -v grep | awk -F ' ' '{print$2}' )

    if [ ! -z $ProzessId ]
      then
      echo "Stoppe nun den Prozess $ProzessId "
      kill -9 $ProzessId
    fi
  fi
 done

;;

 *)
  echo "Usage:    $0 {start|stop}"
  exit 0
;;
esac


#set -x
#echo "bis hier erstmal" && exit 0      #  debug
#done                                   #  debug
