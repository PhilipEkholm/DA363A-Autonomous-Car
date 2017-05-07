#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Thu May 4 23:05:18 2017

@author: henrikfredlund
"""

import cv2 
import glob
import os
path = '/Users/henrikfredlund/Documents/Skola/P1/henrik right'
index = 0
for filename in glob.glob(os.path.join(path, '*.jpg')):
    output = cv2.resize(cv2.imread(filename), (50,50))
    gray_image = cv2.cvtColor(output, cv2.COLOR_BGR2GRAY)
    cv2.imwrite('/Users/henrikfredlund/Python/Högerskylt/' + str(index) + '.jpg', gray_image)    
    index += 1
    print filename