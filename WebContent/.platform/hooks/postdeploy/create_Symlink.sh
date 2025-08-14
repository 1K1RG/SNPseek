#!/bin/bash

LOGFILE="/var/log/new_symlink_creation.log"
      
echo "Starting symlink creation at $(date)" >> $LOGFILE
      
if [ -d /var/lib/tomcat9/webapps/temp ]; then
	echo "temp Directory exists"
	rm -rf /var/lib/tomcat9/webapps/temp
else
	echo "temp Directory does not exist"
fi
      
echo "CREATING SYMLINK...2"
echo "sudo -u tomcat ln -sf /mnt/efs/iric-portal-files/temp /var/lib/tomcat9/webapps/temp"
sudo ln -sf /IRCStorage/iric-portal-files/temp /var/lib/tomcat9/webapps/temp >> $LOGFILE 2>&1
sudo ln -sf /IRCStorage/jbrowse /var/lib/tomcat9/webapps/jbrowse >> $LOGFILE 2>&1
sudo ln -sf /IRCStorage/iric-portal-files/static/gwas /var/lib/tomcat9/webapps/static >> $LOGFILE 2>&1
sudo ln -sf /IRCStorage/rice-ideogram /var/lib/tomcat9/webapps/rice-ideogram >> $LOGFILE 2>&1
      
      
if [ $? -eq 0 ]; then
	echo "Symlink created successfully" >> $LOGFILE
else
	echo "Failed to create symlink" >> $LOGFILE
fi
      
if [ -d /var/lib/tomcat9/webapps/temp ]; then
	echo "webapps/temp Directory exists" >> $LOGFILE 2>&1
	ls -l /var/lib/tomcat9/webapps/ >> $LOGFILE 2>&1
	echo "=========" >> $LOGFILE 2>&1
	ls -l /var/lib/tomcat9/webapps/ROOT >> $LOGFILE 2>&1
else
	echo "webapps/temp Directory does not exist" >> $LOGFILE 2>&1
fi

