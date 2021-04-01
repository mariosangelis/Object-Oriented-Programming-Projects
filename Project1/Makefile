
run: size contains preorder distant_words prefixed_words 


size: size1 size2 size3
size1:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/small.dict size > tests/size1.out                                                   
	diff tests/size1.std tests/size1.out
size2:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/large.dict size> tests/size2.out                                                    
	diff tests/size1.std tests/size1.out
size3:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/huge.dict size> tests/size3.out                                                     
	diff tests/size1.std tests/size1.out

contains: contains1 contains2
contains1:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/large.dict contains_word sugarcoating > tests/contains1.out
	diff tests/contains1.std tests/contains1.out
contains2:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/large.dict contains_word makefaster > tests/contains2.out
	diff tests/contains2.std tests/contains2.out

preorder: preorder1 preorder2 preorder3
preorder1:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/tiny.dict print_preorder > tests/preorder1.out
	diff tests/preorder1.std tests/preorder1.out
preorder2:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/mini.dict print_preorder > tests/preorder2.out
	diff tests/preorder2.std tests/preorder2.out
preorder3:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/small.dict print_preorder > tests/preorder3.out
	diff tests/preorder3.std tests/preorder3.out

distant_words: distant_words_11 distant_words_12 distant_words_13 distant_words_14 distant_words_21 distant_words_22 distant_words_23 distant_words_31
	
distant_words_11:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/mini.dict distant_words 1 aab > tests/distant_words_11.out
	diff tests/distant_words_11.std tests/distant_words_11.out
distant_words_12:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/small.dict distant_words 1 bide > tests/distant_words_12.out
	diff tests/distant_words_12.std tests/distant_words_12.out
distant_words_13:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/mini.dict distant_words 1 mas > tests/distant_words_13.out
	diff tests/distant_words_13.std tests/distant_words_13.out
distant_words_14:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/large.dict distant_words 1 sogar > tests/distant_words_14.out
	diff tests/distant_words_14.std tests/distant_words_14.out
distant_words_21:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/mini.dict distant_words 2 aab > tests/distant_words_21.out
	diff tests/distant_words_21.std tests/distant_words_21.out
distant_words_22:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/small.dict distant_words 2 hey > tests/distant_words_22.out
	diff tests/distant_words_22.std tests/distant_words_22.out
distant_words_23:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/large.dict distant_words 2 oogar > tests/distant_words_23.out
	diff tests/distant_words_23.std tests/distant_words_23.out
distant_words_31:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/large.dict distant_words 3 makfeje > tests/distant_words_31.out
	diff tests/distant_words_31.std tests/distant_words_31.out
distant_words_32:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/large.dict distant_words 3 papakip > tests/distant_words_32.out
	diff tests/distant_words_32.std tests/distant_words_32.out

prefixed_words: prefixed_words1 prefixed_words2 prefixed_words3
prefixed_words1:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/large.dict prefixed_words sugar > tests/prefixed_words1.out
	diff tests/prefixed_words1.std tests/prefixed_words1.out
prefixed_words2:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/large.dict prefixed_words make > tests/prefixed_words2.out
	diff tests/prefixed_words2.std tests/prefixed_words2.out
prefixed_words3:
	java -cp build/classes/ ce326.hw1.HW1 dictionaries/large.dict prefixed_words star > tests/prefixed_words3.out
	diff tests/prefixed_words3.std tests/prefixed_words3.out

clean:
	@rm tests/*.out
