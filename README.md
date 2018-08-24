# markov-spark
Creates the markov chain from files in a directory in parallel. The records produced are in the following format:

keyWord1 SPACE keyWord2 (...rest of keywords) TAB nextWord1 SPACE cummulativeProbability TAB (...rest of word/probability pairs)

Example output:

Hors d'oeuvres, and 1.0 which 0.75      she 0.5 assorted 0.375  said 0.25       salad, 0.125

Maryland, marched       gallantly 1.0

Longobards themselves.  Symonds 1.0

Pontifical Curia,       at 1.0

their syllabic  charm. 1.0000000000000002       analysis. 0.8888888888888891    value 0.7777777777777779        divisions 0.6666666666666667    quantities 0.5555555555555556   combinations 0.4444444444444444 combinations. 0.3333333333333333        correspondence 0.2222222222222222

The [markov-text-generator](https://github.com/jrciii/markov-text-generator) project reads these files to generate text!
