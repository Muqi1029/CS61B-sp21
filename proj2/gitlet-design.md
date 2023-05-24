# Gitlet Design Document

**Name**: Muqi

## 1. the characteristics of git
- written by C
- rely on many data structures
  - maps
  - hash (use it as version number)
  - file I/O
  - graphs

## 2. the main terms for git
1. blobs
2. commits
3. tree

## 3. Classes and Data Structures

### 3.1 Commit


#### 3.1.1 Instance Variables

1. message: contains the message of a commit
2. parent(Commit): conserve the reference to its parent 
3. map 
   - from fileName to sha1, eg: a.txt --> sdha3232dsa

#### ??how to record blobs

### Blob

#### Fields

1. hash
2. file


## 4. Algorithms

## 5. Persistence
- gitlet
  - commit: conserve Object commit
  - stage: stage area
    - shahae1dewfe
    - dhsddcid3294
  - blob: conserve Object blob
  - head: conserve current commit