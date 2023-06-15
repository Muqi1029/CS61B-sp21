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
> Combinations of log messages, other metadata, a reference to a tree, 
> and references to parent commits. The repository also maintains a mapping
> from *branch heads* to references to commits, so that certain important commits 
> have symbolic names.

#### 3.1.1 Fields

1. message(String): contains the message of a commit
2. parent(Commit): conserve the reference to its parent 
3. timestamp(Date): the commit time
4. map(Treemap)
   - from fileName to sha1, eg: a.txt --> sdha3232dsa
5. secondParent(Commit): conserve the reference to its second parent(if this commit is born by merge operation)

### 3.2 branch
#### 3.2.1 Fields
1. name(String): record the name of this branch
2. commit(Commit): record the newest commit of this branch


## 4. Algorithms

## 5. Persistence
- .gitlet: hidden file
  - commit: conserve Object commit
  - stage: stage area
    - shahae1dewfe (the content of file1.txt)
    - dhsddcid3294 (the content of file2.txt)
    - map(TreeMap.class, this field is used to preserve the information of stage area): file name -> sha1
    - removal(ArrayList.class): store the files that are deleted 
  - blob: conserve file blob
    - name: dasdsdsdsads (store the content of file)
  - head(Commit.class): conserve current commit
  - branch(ArrayList.class): preserve the all branches of this repository
    - the front element of branch is the current branch