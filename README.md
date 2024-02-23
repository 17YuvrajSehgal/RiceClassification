##Rice Classification

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
3. GP parameter:
   ![image](https://github.com/17YuvrajSehgal/RiceClassification/assets/81456735/e2ceeb56-069b-438b-9559-131c9e4ab778)


##Results
The tests and traning accuracy was upto 93%.

Adjusted fitness over 10 runs
![image](https://github.com/17YuvrajSehgal/RiceClassification/assets/81456735/d7f712a5-e182-4593-b3fd-82bbae012b3a)

Average fitness over 10 runs
![image](https://github.com/17YuvrajSehgal/RiceClassification/assets/81456735/c50cf2d8-a296-4196-81ed-1d48db245d9d)

Training hits VS Testing hits
![image](https://github.com/17YuvrajSehgal/RiceClassification/assets/81456735/5bfb3955-2d4d-4294-809d-6a6a5b7c97db)




## License

This project is licensed under the MIT License - see the LICENSE.md file for details
