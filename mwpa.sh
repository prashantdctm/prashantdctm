#!/bin/bash
export DISPLAY=:1
APPDIR=/opt/eclipse-workspace/mwpa-agent-inboxdollar/
today=$(date '+%Y-%m-%d %H:%M:%S')

echo "Starting mwpa on $today"

#if running from cron (on reboot), wait for 1 min.
if [ -z "$1" ]
then
  echo "Sleeping for 2 seconds"
  sleep 2
else
  echo "Sleeping for 60 seconds"
  sleep 60
fi

xdg-open https://www.inboxdollars.com/search/infospace?query=
#google-chrome https://www.inboxdollars.com/search/infospace?query=

sleep 5

searchedToday=$(java -cp $APPDIR/config:$APPDIR/bin com.mwpa.agent.IDAgent)

echo "search today = $searchedToday"
if [ "$searchedToday" = "1" ] 
then
  echo "No search today"
else
  echo "Check account"
  #Check Account
  xdg-open https://www.inboxdollars.com/account-statement
  #google-chrome https://www.inboxdollars.com/account-statement
fi

pkill --oldest --signal TERM -f chrome
