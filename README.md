Rice Classification

This project is about classifying two types of rice: Cammeo and Osmancik. The classification problem is from the UCI Machine Learning Repository.

## Dataset

The dataset consists of 3810 records (rice images). Each record has the following numeric attributes:
Cite: Rice (Cammeo and Osmancik). (2019). UCI Machine Learning Repository. https://doi.org/10.24432/C5MW4Z.

1. Area
2. Perimeter
3. Major_Axis_Length
4. Minor_Axis_Length
5. Eccentricity
6. Convex_Area
7. Extent
8. Class: (binary) This is the class label for the record.

## Approach

1. The database is read into a large table (2D array), one row per example. The rows are randomly shuffled. Then the table is split into two independent sets of examples – a training set, and a testing set.

2. A set of language primitives is defined to be used by GP. These primitives should work sensibly on the input data. A possible set of primitives used here are:
   - functions: arithmetic operators, trigonometric operators, min and max
   - terminals: Data attribute variables (7), and ephemeral random constants.

##Results

## License

This project is licensed under the MIT License - see the LICENSE.md file for details
