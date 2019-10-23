#!/bin/bash

echo "Getting updates and installing utilities required for Photocentric Pi4 setup"
    sudo apt-get update
    sudo apt-get -y upgrade
    sudo apt-get -y install xserver-xorg xinit xorg openjdk-8-jdk matchbox-window-manager dos2unix usbmount fbi
    sudo rpi-update

echo "Removing pi branding,boot messages etc"
	echo -n "quiet loglevel=3 logo.nologo consoleblank=0" > /boot/cmdline.txt

echo "Disabling Screensaver"

    if [ -e "/etc/X11/xinit/xinitrc" ]
	    then
		    echo \#\!/bin/bash > /etc/X11/xinit/xinitrc
		    echo xset s off >> /etc/X11/xinit/xinitrc
		    echo xset -dpms >> /etc/X11/xinit/xinitrc
		    echo xset s noblank >> /etc/X11/xinit/xinitrc
	    else
		    echo "xinit doesn't exist or not installed"
    fi

echo "Modifying config.txt file for touchscreen and 4K printscreen"
		echo disable_splash=1 >> /boot/config.txt
		echo avoid_warnings=1 >> /boot/config.txt
        echo disable_overscan=1 >> /boot/config.txt
        echo hdmi_force_hotplug=1 >> /boot/config.txt
        echo config_hdmi_boost=4 >> /boot/config.txt
        echo hdmi_enable_4kp60=1 >> /boot/config.txt
        echo hvs_priority=0x32ff >> /boot/config.txt

echo "Installing kweb browser"
	# Since this isn't on standard apt-get sources, i'm keeping this as an alternative way to source kweb. |This isn't the latest kweb!!!
	sudo wget http://steinerdatenbank.de/software/kweb-1.7.9.7.tar.gz
	sudo tar -xzf kweb-1.7.9.7.tar.gz
	cd kweb-1.7.9.7
	./debinstall
	rm -rf kweb-1.7.9.7

echo "Setting up kiosk-only mode"
		echo \#\!/bin/bash > /home/pi/.xsession
		echo xset s off >> /home/pi/.xsession
		echo xset -dpms >> /home/pi/.xsession
		echo xset s noblank >> /home/pi/.xsession    


echo unclutter -jitter 1 -idle 0.2 -noevents -root \& feh -NY --bg /etc/splash.png /etc/ \& exec matchbox-window-manager -use_titlebar no 

echo "Creating auto mount rules" 
    cat > /etc/udev/rules.d/11-media-by-label-auto-mount.rules
    echo KERNEL!="sd[a-z][0-9]", GOTO="media_by_label_auto_mount_end" >> /etc/udev/rules.d/11-media-by-label-auto-mount.rules
    echo IMPORT{program}="/sbin/blkid -o udev -p %N" >> /etc/udev/rules.d/11-media-by-label-auto-mount.rules
    echo ENV{ID_FS_LABEL}!="", ENV{dir_name}="%E{ID_FS_LABEL}" >> /etc/udev/rules.d/11-media-by-label-auto-mount.rules
    echo ENV{ID_FS_LABEL}=="", ENV{dir_name}="usbhd-%k" >> /etc/udev/rules.d/11-media-by-label-auto-mount.rules
    echo ACTION=="add", ENV{mount_options}="relatime" >> /etc/udev/rules.d/11-media-by-label-auto-mount.rules
    echo ACTION=="add", ENV{ID_FS_TYPE}=="vfat|ntfs", ENV{mount_options}="$env{mount_options},utf8,gid=100,umask=002" >> /etc/udev/rules.d/11-media-by-label-auto-mount.rules
    echo ACTION=="add", RUN+="/bin/mkdir -p /media/%E{dir_name}", RUN+="/bin/mount -o $env{mount_options} /dev/%k /media/%E{dir_name}" >> /etc/udev/rules.d/11-media-by-label-auto-mount.rules
    echo ACTION=="remove", ENV{dir_name}!="", RUN+="/bin/umount -l /media/%E{dir_name}", RUN+="/bin/rmdir /media/%E{dir_name}" >> /etc/udev/rules.d/11-media-by-label-auto-mount.rules
    echo LABEL="media_by_label_auto_mount_end" >> /etc/udev/rules.d/11-media-by-label-auto-mount.rules

echo "Making auto USB mount to work"
udevadm control --reload-rules


rm -rf photonic_setup.sh