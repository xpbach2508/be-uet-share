# ProphetShare Dataset

This repository contains the dataset used for the ProphetShare project.

## Backend Database

The `optimal_schedule.sql` file is used as the backend database for this project.

## Data Collection Process

To make requests from 250 schools in Hanoi, follow these steps in the `/data-collection`:

1. **Run `scrapedata.py`**

   - This script gathers all the schools in Hanoi
   - **Output**: `schools.json`

2. **Run `formatSchoolData`**

   - This script formats the school data to make it easier to process
   - **Input**: `schools.json`
   - **Output**: `hanoi_schools.json`

3. **Run `checkCondition.py`**

   - This script ensures that the request is in the grid referred to in the thesis

4. **Run `generateData.py`**
   - This script creates the requests based on the processed data

## Experiment Results

The `experiment` folder contains all the experimental results and data analysis.
`/experiment/statis.py` code is used to generate visualized performance data.

## API Documentation

The `api_experiment.postman_collection.json` file contains the complete list of API endpoints and their documentation in Postman format.
