#!/usr/bin/env bash

git add .
git commit -m "自动提交:$1"
git push origin master
ssh root@39.97.219.174 "cd /root/accident-server-2 && bash run.sh"