ngram-count -text oracle_sequence_train_data.txt -order 3 -write train.count
ngram-count -read train.count -order 3 -lm train.lm -interpolate -kndiscount
