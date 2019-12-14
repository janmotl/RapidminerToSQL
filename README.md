GLM to SQL
==========
What's nice about logistic regression is that the scoring can be done in a simple SQL query (it's just a weighted average transformed with a sigmoid). But how to persuade Rapidminer to generate the SQL?

Assuming that your model is generated with _Generalized Linear Model_ operator, pass the trained model to _Execute Script_ operator. Copy paste the content of `glm2sql.java` into _Execute Script_. It will generate the core of SQL that may look like:
```sql
select "ID"
      , 1/(1 + exp(-(-7.2194 + "ADRESS_COUNT" + "ADRESS_TYPE_LAST = temporal"))) as "PREDICTED_PROBABILITY"
from (
  select "ID"
        , 0.0688 * "ADRESS_COUNT" as "ADRESS_COUNT"  -- Example of a numeric feature
        , case when "ADRESS_TYPE_LAST" = 'temporal' then 0.7613 else 0 end as "ADRESS_TYPE_LAST = temporal"  -- Example of a nominal feature
  from "MAINSAMPLE"
) t1
  ```

__Supported features__
1. Linear and logistic regression (with logit linkage)
2. Numerical and nominal features

GLM to Table
============
If you want to extract the coefficients and store them into the database (e.g.: for a dashboard), use `glm2table.java`, which returns ExampleSet. 

Contribution
============
Pull requests are welcomed.
