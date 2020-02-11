# SMARTICS APP

## Description and Specification
This is a Springboot application to run with MariaDB as Database accessed via JPA, enabled REST access. As package manager Maven is used.
It runs with Java OpenJDK 8 to 11.
Time to setup: (depending on linux experience level) 5 to 20 mins.


## ChangeLog:

v0.1.3
- added powerchart to visualize total metering data (produced vs consumed)
- visualized status for metering data in energychart (green line/circles on not full data)
- fixed error on bad charger response data
- added bootstrap and responsive layout

v0.1.2
- smart charging including evaluation every rough/5-min data
- added react frontend with basic charger overview and settings
- added d3 energy chart for inverter overview incl. consumption and production data
- additional settings check and setup on startup of app

v0.1.1
- inverter repository and service to access basic inverter data
- charger repository and service to access basic charger data
- scheduler for detail/rough/daily inverter data summaries
- setting repository and service to access basic setting data


## Installation
pack as jar with: mvn install
and run with (on f.e. a raspberry pie 3) or automatic as a service like described below:
> java -jar smartics.jar -Dspring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect

### prepare database and Java

> sudo apt-get update

use a current java version f.e., or because of non compliant to ARMv7+ VFP use jre 8 for a raspberry 1:
be patient with a pi 1 though (test took 390 seconds to start SmarticsApplication - versus 56 secs on a pi 3)!
> sudo apt-get install openjdk-11-jre
> sudo apt-get install openjdk-8-jre

> sudo apt-get install mariadb-server mariadb-client

> sudo mariadb
> create user smartics identified by 'smartics';
> create database smartics;

additionally grant access by user and locally:
the same can be added by replacing 'localhost' with the ip of the remote system of choice for remote db access.

> GRANT USAGE ON *.* TO 'smartics'@localhost IDENTIFIED BY 'smartics';
> GRANT ALL privileges ON smartics.* TO 'smartics'@localhost;
> FLUSH PRIVILEGES;
> quit

> sudo service mariadb restart

> sudo mariadb -u smartics -p

### run automatic on startup

> sudo nano /etc/rsyslog.d/smartics.conf

if $programname == 'smartics' then /var/log/smartics-0.1.log
if $programname == 'smartics' and $msg contains 'ERROR' then /var/log/smartics-0.1-error.log
if $programname == 'smartics' then stop

> sudo systemctl restart rsyslog

> sudo nano /lib/systemd/system/smartics.service

smartics.service:

 [Unit]
 Description=Smartics
 After=multi-user.target

 [Service]
 Type=idle
 WorkingDirectory=/opt/smartics
 ExecStart=/usr/bin/java -jar smartics.jar
 StandardOutput=syslog
 StandardError=syslog
 SyslogIdentifier=smartics

 [Install]
 WantedBy=multi-user.target

> sudo chmod 644 /lib/systemd/system/smartics.service
> sudo systemctl daemon-reload
> sudo systemctl enable smartics.service
> sudo systemctl start smartics.service

### other helpful things

download f.e. raspbian buster lite from: https://www.raspberrypi.org/downloads/raspbian/
use f.e. etcher to install on sd card: https://etcher.download/

change standard password or create new user/group
> sudo passwd pi

connect to raspberry pi from win: (use f.e. putty.exe after enabling ssh on the pi)
check your ip address:
> ifconfig

set correct time/zone (should be similar to the one on the other devices) f.e.:
> sudo raspi-config

enable ssh on pi
> cd /boot
> sudo touch ssh
> sudo shutdown -r now

connect to mariadb remotely (skip-binding in my.cnf; grant user-access):
> sudo nano /etc/mysql/my.cnf

 [mysqld]
 skip-networking=0
 skip-bind-address

> sudo service mariadb restart

install pscp.exe for accessing and copying files: (use location of jar and tmp folder on pi)
> ./pscp.exe d:/development/workspace2019/smartics/target/smartics-0.1.3-SNAPSHOT.jar pi@192.168.1.162:/tmp/smartics-0.1.3.jar

copy file from temp folder:
> sudo mkdir /opt/smartics
> sudo mv /tmp/smartics-0.1.3.jar /opt/smartics/

restart service after update:
> sudo systemctl restart smartics.service

inspect output of smartics/log:
> tail /var/log/smartics-0.1.log -f


## bugs
- charger: error on status-set leads to error in ChargerRepositoryImpl leading to killing of scheduler!!
- inverterarchiverecalc produces daily summary, leading to 416/missing data if done for today (won't override at end of day)
- inverterarchiverecalc produces rough/min entry for current time that won't be overridden in next run (rough already ran)
~ energyChart: consumption can be more: 7,000 Wh - paint extrem values extra?
+ energyChart: production values over night are 0 - skip rect for those values! (do not add to array)
- archive data: correct 1st entry min of day not gotten and visualization in consumed-graph/path


## todos
- fix liquibase (add correctly to project, generate diff)
- handle inverter errors
+ add archived data from inverter
- cleanup springboot (remove unnecessary dependencies)
- move node_modules out of src (for easier handling, backup)

+ general graphical view (produced/consumed/feedback data)
- graphical view of produced=consumed/feedback, consumed=from grid/produced
~ responsive design (to use on mobile and screen)
- enable analysis of consumed power (add logic to calculate spikes,..)
+ add go-e charger access (set ampere according to +energy available)
- add go-e charger funktionality of setting end ampere
- add historical charging data (+repo, graph?)
- add configuration (for inverter ip, cost of kw hour,..)
- add time difference calculation


## stats

- raspberry pi 3 reboot time (from last entry to next entry in mariadb): ~90s
- raspberry pi 3 smartics startup time (including db initialization): ~60s
- entries every 5 seconds: 17.280 a day, 6.307.200 a year, ~450mb
- entries every 5 minutes: 288 a day, 105.120 a year, ~10mb

