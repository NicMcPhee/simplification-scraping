# simplification-scraping

A simple Clojure script to scrape data out of @thelmuth's dissertation Clojush runs.
We're exploring alternative simplification mechanisms that modify the genomes instead of the programs directly, and needed a bunch of genomes and their associated training and testing cases.

This outputs the scraped data as a single map in EDN format. The map contains the following fields:

* `:success`: a boolean value that indicates whether the run was successful or not. That should be `true` in all generated EDN files since the script doesn't generate a file if the run wasn't successful.
* `:success-generation`: The (first) generation when a successful individual was generated, which will be the last generation of the run since all these runs are set to terminate as soon as a solution is discovered.
* `:training-cases`: A vector of training cases. Each training case is itself a vector with two elements: The input(s) and the expected output(s). These can be vectors if there are multiple inputs or multiple outputs.
* `:test-cases`: A vector of test cases, with the same structure as `:training-cases`.

## Usage

This gives you a script that 1 or more log files as command line arguments and writes the results in EDN files. If, for example, you specific `log0.txt` as one of the input files, the script will output the desired information in EDN format in `log0.edn`.

    $ lein run log0.txt log1.txt...
    $ lein run log*.txt

## License

Copyright © 2016 Nic McPhee

Distributed under the MIT License.