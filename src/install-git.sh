#/**
# * � Copyright IBM Corporation 2014.  
# * This is licensed under the following license.
# * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
# * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp. 
# */

#!/bin/bash

# Define functions

yumInstall() {
			echo
			echo "Calling yum to install git...."
			echo 
			sudo yum -y install git-core
			if [ $? -ne 0 ]; then
				echo
				echo "YUM install has failed"
				echo
				exit 1
			fi
}

zypperInstall() {
			echo "Calling zypper to install git...."
			echo 
			sudo zypper -n install git-core
			if [ $? -ne 0 ]; then
				echo
				echo "YUM install has failed"
				echo
				exit 1
			fi
}

aptgetInstall() {
			echo "Ensure we have the pre-reqs installed..."
			sudo apt-get --fix-missing -q -y install libcurl4-gnutls-dev libexpat1-dev gettext libz-dev libssl-dev
			if [ $? -ne 0 ]; then
				echo
				echo "Install of pre-reqs has failed"
				echo
				exit 1
			fi
			echo "Calling apt-get to install git..."
			sudo apt-get --fix-missing -q -y install git
			if [ $? -ne 0 ]; then
				echo
				echo "apt-get install has failed"
				echo
				exit 1
			fi
}

rhelInstall() {
			echo "Pull down git tar files..."
			wget http://git-core.googlecode.com/files/git-1.8.3.4.tar.gz
			if [ $? -ne 0 ]; then
				echo
				echo "download of git tar filed has failed"
				echo
				exit 1
			fi
			wget -O git-manpages-1.8.3.4.tar.gz http://code.google.com/p/git-core/downloads/detail?name=git-manpages-1.8.3.4.tar.gz&can=2&q=
			if [ $? -ne 0 ]; then
				echo
				echo "download of git man pages failed"
				echo
				exit 1
			fi
			echo "Install pre-reqs for building git..."
			sudo yum -y install zlib-devel perl-CPAN gettext
			if [ $? -ne 0 ]; then
				echo
				echo "install of pre-reqs failed"
				echo
				exit 1
			fi
			echo "Untar git code..."
			tar xvfz git-1.8.3.4.tar.gz
			if [ $? -ne 0 ]; then
				echo
				echo "untar of code failed"
				echo
				exit 1
			fi
			cd git-1.8.3.4
			echo "configure ready for make to run..."
			./configure
			if [ $? -ne 0 ]; then
				echo
				echo "configure for git failed"
				echo
				exit 1
			fi
			echo "running make..."
			make
			if [ $? -ne 0 ]; then
				echo
				echo "make of git failed"
				echo
				exit 1
			fi
			echo "Install in to /usr..."
			sudo make prefix=/usr install
			if [ $? -ne 0 ]; then
				echo
				echo "install of git failed"
				echo
				exit 1
			fi
}


echo ================================================================
echo "Starting installation of git"
echo ================================================================
echo
echo "Checking if we have git installed already..."
command -v git
if [ $? -eq 0 ]; then
	echo
	echo "git is installed"
	echo
	exit 0
fi

echo "Checking which Linux distro is installed on target server...."


detectedDistro="Unknown"
regExpLsbInfo="Description:[[:space:]]*([^ ]*)"
regExpLsbFile="/etc/(.*)[-_]"

if [ `which lsb_release 2>/dev/null` ]; then       # lsb_release available
   lsbInfo=`lsb_release -d`
   if [[ $lsbInfo =~ $regExpLsbInfo ]]; then
      detectedDistro=${BASH_REMATCH[1]}
   else
      echo "??? Should not occur: Don't find distro name in lsb_release output ???"
      exit 1
   fi

else                                               # lsb_release not available
   etcFiles=`ls /etc/*[-_]{release,version} 2>/dev/null`
   for file in $etcFiles; do
      if [[ $file =~ $regExpLsbFile ]]; then
         detectedDistro=${BASH_REMATCH[1]}
         break
      else
         echo "??? Should not occur: Don't find any etcFiles ???"
         exit 1
      fi
   done
fi

detectedDistro=`echo $detectedDistro | tr "[:upper:]" "[:lower:]"`

case $detectedDistro in
	suse) 	detectedDistro="opensuse" ;;
        linux)	detectedDistro="linuxmint" ;;
esac

echo
echo "Detected distro: $detectedDistro"
echo

case "$detectedDistro" in
        centos)
        	echo ===============================================================
			echo "Running Centos installation steps"
			echo ===============================================================
			echo
			yumInstall
			exit 0
            ;;
         fedora)
        	echo ===============================================================
			echo "Running Fedora installation steps"
			echo ===============================================================
			echo
			yumInstall
			exit 0
            ;;        
        red)
        	echo ===============================================================
			echo "Running RedHat installation steps"
			echo ===============================================================
			echo
			rhelInstall
			exit 0
            ;;
        redhat)
        	echo ===============================================================
			echo "Running RedHat installation steps"
			echo ===============================================================
			echo
			rhelInstall
			exit 0
            ;;
        ubuntu)
        	echo ===============================================================
			echo "Running Ubuntu installation steps"
			echo ===============================================================
			echo
			aptgetInstall
			exit 0
            ;;         
        debian)
        	echo ===============================================================
			echo "Running Debian installation steps"
			echo ===============================================================
			echo
			aptgetInstall
			exit 0
            ;;
        opensuse)
        	echo ===============================================================
			echo "Running OpenSUSE installation steps"
			echo ===============================================================
			echo
			zypperInstall
			exit 0
            ;;
        *)
            echo $"Linux distribution detected is unsupported by this plugin - sorry :-("
            exit 1
 
esac

exit 0
