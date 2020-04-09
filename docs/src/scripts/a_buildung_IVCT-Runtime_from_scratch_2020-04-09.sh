#! /bin/bash
#
# a_buildung_IVCT-Runtime_from_scratch_202Y-MM-DD.sh
#
# 10.03.2020 S.Barfuss, Fraunhofer IOSB
# Fraunhofer Institut für Optronik,Systemtechnik und Bildauswertung
#
#
# Info and Script to build a IVCT_Runtime environment with all necessary
# Parts from
#
# and to renew the different elements when they are new compiled
#
################################################################
#
# versionen      brf
# 20200409  brf  adding a link HelloWorld -> HelloWorld-2.N.N
# 20200331  brf  prepare the script for use with 'Windows Services for Linux'
# 20200330  brf  Informations about necessity to install and configurate git
# 20200320  brf  provide a IVCT.properties with suitable values
# 20200319  brf  change text and varibles 
# 20200318  brf  a first approach to test 
# 20200310  brf  Beginning a new Version of this Script for whole Runtime-environment
#
################################################################

#-----------------------------------------------------------------------------
# prerequisites
#
# java , jdk or jre  somewhere on the system
#
# downloaded apache-tomcat-8.5.XX.tar.gz and apache-activemq-5.15.XX-bin.tar.gz
# (if you later want do copy to Windows, you need to download Windows Versions)
# as described in Section 2
# 
# install git (apt-get install git)  and configure it to use a proxy
# 'git config --global http.proxy http://proxy-Yours.inst.firma.de:3128'
#
# RTI  a installation of rti prti  which can be reached and used
# a free version is available at http://pitchtechnologies.com/products/prti/
# install prti1516efree_5_3_2_1_linux64_b140.deb  as root 
# you may need these variables in your IVCT-Startscript
# export PRTI1516E_HOME=/opt/prti1516e
# export LRC_HOME=$RTI_HOME
# export LRC_CLASSPATH=${PRTI1516E_HOME}/lib/prti1516e.jar



# ----------------------------------------------------------------------------
# 1 Variables and Pathes
# ----------------------------------------------------------------------------

# for compilation we need java  JDK somewhere on our system
export JAVA_HOME=/opt/jdk1.8.0_161

# in this Dir  we install all             ### change this to your path 
export IVCTTOOL_Dir=/opt/IVCTool_c
#export IVCTTOOL_Dir=/opt/IVCTool

# our 'Runtime-environment'
export RUNTIME_Dir=${IVCTTOOL_Dir}/IVCT_Runtime

## this has to be done by root !!!!
[ -d $RUNTIME_Dir ] ||  sudo  mkdir -p $RUNTIME_Dir

#sudo chown -R yourUser  IVCTTOOL_Dir     ### change this to your user
sudo chown -R brf  $IVCTTOOL_Dir


### the different Source- and compile - Dirs
# Framework
export Framework_Dir=${IVCTTOOL_Dir}/IVCT_Framework

# Testsuite HLA_BASE 
export TS_HLA_BASE_Dir=${IVCTTOOL_Dir}/TS_HLA_BASE

# HalloWorld and  sample test suite
export TestSuiteDevelopment_Dir=${IVCTTOOL_Dir}/IVCT_TestSuiteDevelopment

datum=`date +%Y-%m-%d`
zeit=`date +%H-%M`



# -----------------------------------------------------------------------------
# 2 Fetching and preparing some required software
# -----------------------------------------------------------------------------

pushd ${IVCTTOOL_Dir}

pushd  $RUNTIME_Dir  > /dev/null

### Apache-Tomcat    we need a tomcat - Installation
# fetch apache-tomcat from http://tomcat.org
# e.g. apache-tomcat-8.5.51.tar.gz  to /opt/temp_src
#      apache-tomcat-8.5.53.windows-x64.zip for use in Windows

tar -xzf /opt/temp_src/apache-tomcat-8.5.51.tar.gz   ### deactivate after the first run
export tomcat_Dir=${RUNTIME_Dir}/apache-tomcat-8.5.51 

#unzip /opt/temp_src/apache-tomcat-8.5.53-windows-x64.zip # Windows use
#export tomcat_Dir=${RUNTIME_Dir}/apache-tomcat-8.5.53 

## work with a general Tomcat - User
#sudo useradd -s /bin/false/ tomuser
#sudo chown -R tomuser  ${RUNTIME_Dir}/apache-tomcat-8.5.51
#sudo usermod -a -G tomuser  YourUser

# or make YourUser Owner of  tomcat
#sudo chown -R YourUser ${RUNTIME_Dir}/apache-tomcat-8.5.51  ### change this to your User
sudo chown -R brf ${tomcat_Dir}

# Later You may have to edit the file ${RUNTIME_Dir}/apache-tomcat-8.5.51/conf/server.xml 


### apache-activemq 
# get from https://activemq.apache.org/components/classic/download/
# e.g. apache-activemq-5.15.11-bin.tar.gz   to temp_src/
#      apache-activemq-5.15.11-bin.zip   for use in Window   for use in Windowss

tar -xzf /opt/temp_src/apache-activemq-5.15.11-bin.tar.gz  ##dectivate after the first run
#unzip /opt/temp_src/apache-activemq-5.15.12-bin.zip  # windows use

#sudo chown -R YourUser  ${RUNTIME_Dir}/apache-activemq-5.15.11
#sudo chown -R brf  ${RUNTIME_Dir}/apache-activemq-5.15.11

popd

read -p "-finished 2 ----- Break ------, [Enter]  to continue, 'ctrl-c' to stop here"

# -----------------------------------------------------------------------------
# 3 Fetching and Building the sources 
# -----------------------------------------------------------------------------
#  IVCT_Framework 
#  IVCT_TestSuiteDevelopment 
#  TS_HLA_Base

#cd ${IVCTTOOL_Dir}

echo -e "\nWe now clone the demanded repositories and compile them"

echo -e "## to use git for cloning you has to set:"
echo    "git config --global http.proxy http://vip-proxy-ka.iosb.fraunhofer.de:3128"
echo    "git config --global https.proxy https://vip-proxy-ka.iosb.fraunhofer.de:3128"

echo -e "\n##for compiling you may need a gradle.properties with:"
echo    "  ossrhUsername=XXX ossrhPassword=myPassword"
echo -e "  and possibly systemProp.http.proxyHost= systemProp.http.proxyPort= \n"

read -p "please copy one to ${IVCTTOOL_Dir} - [Enter]  to continue, 'ctrl-c' to stop here"


### IVCT_Framework                 ### dectivate following after the first run 
git clone  https://github.com/IVCTool/IVCT_Framework.git
cd IVCT_Framework
git checkout development
cp ${IVCTTOOL_Dir}/gradle.properties .
sleep 1
./gradlew install

read -p "-finished IVCT_Framework --- Break ---, [Enter]  to continue, 'ctrl-c' to stop here"
cd ..

### TS_HLA_BASE                     ### dectivate following after the first run
git clone https://github.com/IVCTool/TS_HLA_BASE.git
cd TS_HLA_BASE
git checkout development
cp ${IVCTTOOL_Dir}/gradle.properties .
./gradlew install
cd ..


### IVCT_TestSuiteDevelopment       ### dectivate following after the first run
git clone  https://github.com/IVCTool/IVCT_TestSuiteDevelopment.git
cd IVCT_TestSuiteDevelopment
git checkout development
cp ${IVCTTOOL_Dir}/gradle.properties .
sudo chmod 744 gradlew
./gradlew install
cd ..


read -p "-finished 3 ----- Break ------, [Enter]  to continue, 'ctrl-c' to stop here"



# -----------------------------------------------------------------------------
# 4 Copying/extracting the Components to their position in the Runtime-Envir.
# -----------------------------------------------------------------------------

echo -e "\n ------------------------------------------------------------------------"
echo -e "## IVCT.properties, copying $Framework_Dir/RuntimeConfig/IVCT.properties to $RUNTIME_Dir \n"

[ -d $RUNTIME_Dir/IVCT.properties ] || cp $Framework_Dir/RuntimeConfig/IVCT.properties $RUNTIME_Dir
echo -e "\n!!!! You have to  edit the  ${RUNTIME_Dir}/IVCT.properties File !!!!!"


echo -e "\n ------------------------------------------------------------------------"
echo -e "## Badges, copying Badges Directory from $Framework_Dir/RuntimeConfig/Badges "

[ -d ${RUNTIME_Dir}/Badges ] && mv -f $RUNTIME_Dir/Badges $RUNTIME_Dir/Badges_${datum}_${zeit}
echo -e "- copying Directory $Framework_Dir/RuntimeConfig/Badges to $RUNTIME_Dir \n"

cp -r $Framework_Dir/RuntimeConfig/Badges $RUNTIME_Dir

read -p "------- Break ------, [Enter]  to continue, 'ctrl-c' to stop here"


echo -e "\n ------------------------------------------------------------------------"
echo -e "## IVCTsut-Directory, get it  from IVCT_Framework/RuntimeConfig/IVCTsut"

[ -d $RUNTIME_Dir/IVCTsut ] && mv $RUNTIME_Dir/IVCTsut $RUNTIME_Dir/IVCTsut_${datum}_${zeit}

echo -e "- copying $Framework_Dir/RuntimeConfig/IVCTsut nach $RUNTIME_Dir \n"

cp -r $Framework_Dir/RuntimeConfig/IVCTsut $RUNTIME_Dir

read -p "------- Break ------, [Enter]  to continue, 'ctrl-c' to stop here"


echo -e "\n ------------------------------------------------------------------------"
echo -e "## TestSuites DescriptionFiles, get it from $Framework_Dir/RuntimeConfig/TestSuites "

[ -d $RUNTIME_Dir/TestSuites ] && mv $RUNTIME_Dir/TestSuites $RUNTIME_Dir/TestSuites_${datum}_${zeit}

echo -e "-copy  $Framework_Dir/RuntimeConfig/TestSuites to  $RUNTIME_Dir \n"

cp -r $Framework_Dir/RuntimeConfig/TestSuites $RUNTIME_Dir

read -p "------- Break ------, [Enter]  to continue, 'ctrl-c' to stop here"


echo -e "\n ------------------------------------------------------------------------"
echo -e "## HelloWorld Simulator - Application from IVCT_TestSuiteDevelopment " 

HelloWorld_active=HelloWorld-2.1.1

## save the old Version
[ -d $RUNTIME_Dir/$HelloWorld_active ] && mv $RUNTIME_Dir/$HelloWorld_active/ $RUNTIME_Dir/${HelloWorld_active}_${datum}_${zeit}

## get the version
#HelloWorld_active=$(ls ${TestSuiteDevelopment_Dir}/HelloWorld/build/distributions |grep tar)
# it's possible that we have different Versions, so we decide which to use 
HelloWorldTar=${HelloWorld_active}.tar

echo -e "- extract $TestSuiteDevelopment_Dir/HelloWorld/build/distributions/$HelloWorldTar to $RUNTIME_Dir \n"

tar -xf $TestSuiteDevelopment_Dir/HelloWorld/build/distributions/$HelloWorldTar -C $RUNTIME_Dir

pushd $RUNTIME_Dir
ln -s ${HelloWorld_active} HelloWorld
popd

read -p "------- Break ------, [Enter]  to continue, 'ctrl-c' to stop here"


echo -e "\n ------------------------------------------------------------------------"
echo -e "## LogSink , fetch this application from Framework/LogSink/build/distribution "

LogSinkActive=LogSink-2.2.2-SNAPSHOT

[ -d $RUNTIME_Dir/$LogSinkActive ] && mv $RUNTIME_Dir/$LogSinkActive $RUNTIME_Dir/${LogSinkActive}_${datum}_${zeit}

# it's possible that we have different Versions, so we decide which to use 
LogSinkTar=${LogSinkActive}.tar

echo -e "- extract $Framework_Dir/LogSink/build/distributions/$LogSinkTar to $RUNTIME_Dir \n"

tar -xf $Framework_Dir/LogSink/build/distributions/$LogSinkTar -C $RUNTIME_Dir

read -p "------- Break ------, [Enter]  to continue, 'ctrl-c' to stop here"


echo -e "\n ------------------------------------------------------------------------"
echo -e "## TC.exec , fetch the application from IVCT_Framework/TC.exec/build/distribution"

TCexecActive=TC.exec-2.2.2-SNAPSHOT

[ -d $RUNTIME_Dir/$TCexecActive ] && mv $RUNTIME_Dir/$TCexecActive $RUNTIME_Dir/${TCexecActive}_${datum}_${zeit}

#TCexecTar=$(ls $Framework_Dir/TC.exec/build/distributions/  | grep tar)
# it's possible that we have different Versions, so we decide which to use 
TCexecTar=${TCexecActive}.tar

echo -e "- extract $Framework_Dir/TC.exec/build/distributions/$TCexecTar to $RUNTIME_Dir \n"

tar -xf $Framework_Dir/TC.exec/build/distributions/$TCexecTar -C $RUNTIME_Dir

read -p "------- Break ------, [Enter]  to continue, 'ctrl-c' to stop here"


echo -e "\n ------------------------------------------------------------------------"
echo -e "## UI, get the application from IVCT_Framework/UI/build/distributions" 

UIActive=UI-2.2.2-SNAPSHOT

[ -d $RUNTIME_Dir/$UIActive ] && mv $RUNTIME_Dir/$UIActive $RUNTIME_Dir/${UIActive}_${datum}_${zeit}

# it's possible that we have different Versions, so we decide which to use 
uiTar=${UIActive}.tar

echo -e "- extract $Framework_Dir/UI/build/distributions/$uiTar to $RUNTIME_Dir \n"

tar -xf $Framework_Dir/UI/build/distributions/$uiTar -C $RUNTIME_Dir

read -p "------- Break ------, [Enter]  to continue, 'ctrl-c' to stop here"


# -----------------------------------------------------------------------------
# Extracting prepared  Testsuites to  the Runtime environment
# -----------------------------------------------------------------------------

echo -e "\n ------------------------------------------------------------------------"
echo -e "## TestSuite TS_HelloWorld, get it from the IVCT_TestSuiteDevelopment Repository"

TSHelloWorldActive=TS_HelloWorld-2.1.1

# for $RUNTIME_Dir/TestSuites is new created in 4.4 , there are no older Versions

#TSHelloWorldTar=$(ls ${TestSuiteDevelopment_Dir}/TS_HelloWorld/build/distributions | grep tar)
# it's possible that we have different Versions, so we decide which to use 
TSHelloWorldTar=${TSHelloWorldActive}.tar

echo -e "-extract $TestSuiteDevelopment_Dir/TS_HelloWorld/build/distributions/$TSHelloWorldTar nach $RUNTIME_Dir/TestSuites \n"

tar -xf $TestSuiteDevelopment_Dir/TS_HelloWorld/build/distributions/$TSHelloWorldTar -C $RUNTIME_Dir/TestSuites

read -p "------- Break ------, [Enter]  to continue, 'ctrl-c' to stop here"


echo -e "\n ------------------------------------------------------------------------"
echo -e "## TestSuite different Testsuites from TS_HLA_BASE Repository "

# for $RUNTIME_Dir/TestSuites is new created in 4.4, there are no older Versions

echo -e "\n## $TS_HLA_BASE_Dir /TS_CS_Verification etc. examine the versions \n"

ls $TS_HLA_BASE_Dir/TS_CS_Verification/build/distributions |grep tar
ls $TS_HLA_BASE_Dir/TS_HLA_Declaration/build/distributions |grep tar
ls $TS_HLA_BASE_Dir/TS_HLA_EncodingRulesTester/build/distributions |grep tar
ls $TS_HLA_BASE_Dir/TS_HLA_Object/build/distributions |grep tar
ls $TS_HLA_BASE_Dir/TS_HLA_Services/build/distributions |grep tar

read -p " please check if this versions are correct and used here in the script, ok:  [Enter] "

## TS_CS_Verification
TSVerificationTar=TS_CS_Verification-2.1.0.tar
echo -e "-extract $TS_HLA_BASE_Dir/TS_CS_Verification/build/distributions/$TSVerificationTar to $RUNTIME_Dir/TestSuites \n"

tar -xf $TS_HLA_BASE_Dir/TS_CS_Verification/build/distributions/$TSVerificationTar -C $RUNTIME_Dir/TestSuites

## TS_HLA_Declaration 
TSDeclarationTar=TS_HLA_Declaration-2.1.0.tar
echo -e "-extract $TS_HLA_BASE_Dir/TS_HLA_Declaration/build/distributions/$TSDeclarationTar to $RUNTIME_Dir/TestSuites \n"

tar -xf $TS_HLA_BASE_Dir/TS_HLA_Declaration/build/distributions/$TSDeclarationTar -C $RUNTIME_Dir/TestSuites

## TS_HLA_EncodingRulesTester 
TSEncodingRulesTesterTar=TS_HLA_EncodingRulesTester-2.1.0.tar
echo -e "-extract $TS_HLA_BASE_Dir/TS_HLA_EncodingRulesTester/build/distributions/$TSEncodingRulesTesterTa to $RUNTIME_Dir/TestSuites \n"

tar -xf $TS_HLA_BASE_Dir/TS_HLA_EncodingRulesTester/build/distributions/$TSEncodingRulesTesterTar -C $RUNTIME_Dir/TestSuites

## TS_HLA_Object 
TSObjectTar=TS_HLA_Object-2.1.0.tar
echo -e "-extract $TS_HLA_BASE_Dir/TS_HLA_Object/build/distributions/$TSObjectTar nach $RUNTIME_Dir/TestSuites \n"

tar -xf $TS_HLA_BASE_Dir/TS_HLA_Object/build/distributions/$TSObjectTar -C $RUNTIME_Dir/TestSuites

## TS_HLA_Services 
TSServicesTar=TS_HLA_Services-2.1.0.tar
echo -e "-extract $TS_HLA_BASE_Dir/TS_HLA_Services/build/distributions/$TSServicesTar to $RUNTIME_Dir/TestSuites \n"

tar -xf $TS_HLA_BASE_Dir/TS_HLA_Services/build/distributions/$TSServicesTar -C $RUNTIME_Dir/TestSuites
echo -e "-extract $TS_HLA_BASE_Dir/TS_HLA_Services/build/distributions/$TSServicesTar to $RUNTIME_Dir/TestSuites \n"



# -----------------------------------------------------------------------------
# prepare apache-tomcat with our application, which we fetched in the 1. part 
# -----------------------------------------------------------------------------

echo -e "\n ------------------------------------------------------------------------"
echo -e " prepare apache-tomcat with the *.war files from IVCT_Framework/nato.ivct.gui* \n"

read -p "## apache-tomcat webapps  if you continue the tomcat-deployment will be overwritten, [Enter] to continue  'strg-c'  to stop here "

# move  the former  webapps
[ -d $tomcat_Dir/webapps ] && cp -a $tomcat_Dir/webapps $tomcat_Dir/webapps_${datum}_${zeit} && rm -r $tomcat_Dir/webapps/*

# it's possible that we have different Versions, so we decide which to use 
ivctGuiServer="ivct.gui.server##2.2.2-SNAPSHOT.war"
echo -e "\n- kopiere nun ${Framework_Dir}/GUI/nato.ivct.gui.server.app.war/build/libs/$ivctGuiServer nach $tomcat_Dir/webapps"
cp ${Framework_Dir}/GUI/nato.ivct.gui.server.app.war/build/libs/$ivctGuiServer $tomcat_Dir/webapps

# it's possible that we have different Versions, so we decide which to use 
ivctGuiUiHtml=ivct.gui.ui.html##2.2.2-SNAPSHOT.war
echo -e "\n- kopiere ${Framework_Dir}/GUI/nato.ivct.gui.ui.html.app.war/build/libs/$ivctGuiUiHtml nach $tomcat_Dir/webapps"
cp ${Framework_Dir}/GUI/nato.ivct.gui.ui.html.app.war/build/libs/$ivctGuiUiHtml $tomcat_Dir/webapps


echo -e "\n ------------------------------------------------------------------------"
echo "tomcat-configuration:"
echo -e "If you do not edit server.xml you can reach the server with: \n"
echo "http://localhost:8080/ivct.gui.ui.html (http://192.168.4.2:8080/ivct.gui.ui.html)"

echo -e "\nIf you want to reach the Server with:  http://localhost:8080 "
echo    "You have to edit conf/server.xml or replace it with a simple one"

echo -e "\nan examle is shown at: https://github.com/IVCTool/IVCT_Runtime/tree/master/apache-tomcat-8.5.40/conf"
echo "important is this: <Context path=  docBase= ivct.gui.ui.html##2.2.2-SNAPSHOT  reloadable= true  >  </Context> "



# -----------------------------------------------------------------------------
# preparing the start-configuration, changing values in IVCT.properties
# -----------------------------------------------------------------------------

cp ${RUNTIME_Dir}/IVCT.properties ${RUNTIME_Dir}/IVCT.properties.org

### activate the right Versions for Linux or Windows

# define the pathes for Linux
echo "IVCT_SUT_HOME_ID=${RUNTIME_Dir}/IVCTsut" >>  ${RUNTIME_Dir}/IVCT.properties
echo "IVCT_TS_HOME_ID=${RUNTIME_Dir}/TestSuites" >>  ${RUNTIME_Dir}/IVCT.properties
echo "IVCT_TS_DEF_HOME_ID=${RUNTIME_Dir}/TestSuites" >>  ${RUNTIME_Dir}/IVCT.properties
echo "IVCT_BADGE_HOME_ID=${RUNTIME_Dir}/Badges" >>  ${RUNTIME_Dir}/IVCT.properties
echo "IVCT_BADGE_ICONS=${RUNTIME_Dir}/Badges" >>  ${RUNTIME_Dir}/IVCT.properties

# define the pathes for Windows
export RUNTIME_Win="c:\\ivctools\\IVCT_Runtime"
#echo "IVCT_SUT_HOME_ID=${RUNTIME_Win}\\IVCTsut" >>  ${RUNTIME_Dir}/IVCT.properties
#echo "IVCT_TS_HOME_ID=${RUNTIME_Win}\\TestSuites" >>  ${RUNTIME_Dir}/IVCT.properties
#echo "IVCT_TS_DEF_HOME_ID=${RUNTIME_Win}\\TestSuites" >>  ${RUNTIME_Dir}/IVCT.properties
#echo "IVCT_BADGE_HOME_ID=${RUNTIME_Win}\\Badges" >>  ${RUNTIME_Dir}/IVCT.properties
#echo "IVCT_BADGE_ICONS=${RUNTIME_Win}\\Badges" >>  ${RUNTIME_Dir}/IVCT.properties

echo -e "\n ------------------------------------------------------------------------"
echo    "How to start"
echo -e " ------------------------------------------------------------------------"

echo " What to do to start the system:"

echo -e "\nEdit the ${RUNTIME_Dir}/IVCT.properties file:"
echo -e "\nWe tried to change it with the upper values, but check it."

echo -e "\nfor using the Runtime-Directory in Linux:"
echo "IVCT_SUT_HOME_ID=${RUNTIME_Dir}/IVCTsut"
echo "IVCT_TS_HOME_ID=${RUNTIME_Dir}/TestSuites"
echo "IVCT_TS_DEF_HOME_ID=${RUNTIME_Dir}/TestSuites"
echo "IVCT_BADGE_HOME_ID=${RUNTIME_Dir}/Badges"
echo "IVCT_BADGE_ICONS=${RUNTIME_Dir}/Badges"

echo -e "\nfor using the Runtime-Directory in Windows:"
echo "IVCT_SUT_HOME_ID=${RUNTIME_Win}\IVCTsut"
echo "IVCT_TS_HOME_ID=${RUNTIME_Win}\TestSuites"
echo "IVCT_TS_DEF_HOME_ID=${RUNTIME_Win}\TestSuites"
echo "IVCT_BADGE_HOME_ID=${RUNTIME_Win}\Badges"
echo "IVCT_BADGE_ICONS=${RUNTIME_Win}\Badges"

#echo "IVCT_SUT_HOME_ID=/opt/IVCTool_b/IVCT_Runtime/IVCTsut"
#echo "IVCT_TS_HOME_ID=/opt/IVCTool_b/IVCT_Runtime/TestSuites"
#echo "IVCT_TS_DEF_HOME_ID=/opt/IVCTool_b/IVCT_Runtime/TestSuites"
#echo "IVCT_BADGE_HOME_ID=/opt/IVCTool_b/IVCT_Runtime/Badges"
#echo "IVCT_BADGE_ICONS=/opt/IVCTool_b/IVCT_Runtime/Badges"


echo -e "\nCreate 2 Startscripts, one for IVCT and one for apache-tomcat"


#read -p "------- Break ------, [Enter]  to continue, 'ctrl-c' to stop here"
#echo "only to this point " && exit 0
