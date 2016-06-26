#!/bin/bash
git checkout gh-pages
git merge origin master
git push
git checkout master
