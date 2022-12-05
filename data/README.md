# Reference Models for the PCM

This directory contains PCM reference models which can be compared to automatically updated PCMs in specific cases.

Every reference model is named according to its corresponding commit in the format "Repository-[commit-number]-mu.repository" where mu marks a manually updated PCM. The commits are usually continuously numbered in the order of propagation beginning with 0 for the initial commit.

Please note that an automatic comparison between a manually and automatically updated PCM can result in a JC not equal to 1 because of differing names in the PCMs. In this case, an additional manual comparison needs to be performed.

## TeaStore

The directory `ce7249d149915b9a4425041ed94d70954a8a3486` contains PCMs based on this repositories' state in the corresponding commit while the directory `88c4015eef95daf39b60e7c8a8fed1ca4a4f8a57` contains PCMs for this corresponding commit (newer than the other state).

Within each directory, the models are further divided into four intervals based on the TeaStore versions: I1 ranges between version 1.1 and 1.2, I2 between 1.2 and 1.2.1, I3 between 1.2.1 and 1.3, and I4 between 1.3 and 1.3.1.

## TEAMMATES

The reference models for TEAMMATES are located in the directory `TEAMMATES`.
