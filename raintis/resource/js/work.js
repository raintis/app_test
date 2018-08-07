function a(bb) {
	var j = 10;
	var i;
	var count = 0;
	for (i in j) {
		// console.log(i);
		count = count + i;
	}
	if (bb == 0) {
		bb = 1;
	}
}

a();