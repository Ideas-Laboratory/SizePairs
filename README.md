# SizePairs
This is the implementation for paper [SizePairs: Achieving Stable and Balanced Temporal Treemaps using Hierarchical Size-based Pairing](http://www.yunhaiwang.net/vis2022/SizePairs/). 


### This implementation is heavily borrowed from [open source code](https://github.com/tue-alga/TreemapComparison) of Vernier et al.'s quantitative evaluation paper. 
If you want to run the other algorithms instead of SizePairs, better go to the original one. 
It is also worth mentioning that we also provide the running results as Vernier et al. did on https://eduardovernier.github.io/dynamic-treemap-resources-eurovis/docs/treemaps/runs/. (We also benefit from their sharings as it will be time-comsuming to run the algo on all datasets.) It is available upon request.

## How to use:

Run Simulator to generate treemaps in .rect files. Modify the default settings in Simulator.java.

Run Visualiser to play with the graphical interface. Specify additional datasets and methods in GUI.java.

Run StatisticalParse to generate statistics.
