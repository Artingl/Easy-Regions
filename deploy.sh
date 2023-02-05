#!/bin/sh

set -e

cp /mnt/stuff/workspace/easyrg/build/libs/easyrg-1.0.jar /home/artingl/server/plugins/
echo Copied

rm /home/artingl/server/plugins/EasyRegions/en-messages.yml
rm /home/artingl/server/plugins/EasyRegions/config.yml
rm /home/artingl/server/plugins/EasyRegions/ru-messages.yml