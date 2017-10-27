// Author: Bert De Saffel

#include <stdio.h>
#include <stdlib.h>

#define AANTAL 5

int som(int a, int b){
	return a + b;
}

int verschil(int a, int b){
	return a - b;
}

int product(int a, int b){
	return a * b;
}

void schrijf(const int * t, int aantal){
	int i;
	for(i = 0; i < AANTAL; i++){
		printf("%i ", t[i]);
	}
	printf("\n");
}

void vul_tabel(const int *, const int *, int *, int aantal, int(*)(int,int));

int main(void){
	int a[AANTAL];
	int b[AANTAL];
	int c[AANTAL];
	int i;
	for(i = 0; i < AANTAL; i++){
		a[i] = 10 * i;
		b[i] = i;
	}

	vul_tabel(a, b, c,AANTAL, &som);
	schrijf(c, AANTAL);

	vul_tabel(a, b, c,AANTAL, &product);
	schrijf(c, AANTAL);

	vul_tabel(a, b, c,AANTAL, &verschil);
	schrijf(c, AANTAL);
	return 0;
}

void vul_tabel(const int *a, const int *b, int *c, int size, int(*fun)(int, int)){
	int i;
	for(i = 0; i < size; i++){
		c[i] = fun(a[i], b[i]);
	}
}