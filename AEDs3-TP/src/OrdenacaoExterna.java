import java.io.*;
import java.util.Arrays;
import java.util.*;

public class OrdenacaoExterna {

	public static void sortInit(String filename, int m, int n) throws IOException {
		CRUD crud = new CRUD("../data/arquivo.bin");
		RandomAccessFile input = new RandomAccessFile(filename + ".bin", "rw");
		String[] arqs = createFiles(n+2, filename);
		populateSortFiles (input, arqs, m, n, filename, crud.getMaxId());
		intercalacaoBalanceada(arqs, m, n);
		input.close();
		System.out.println("fechou");
		//crud.mostrarTudo("../data/arquivo2tmp.bin", 0);
	}

	/*
	 * Cria a quantidade de arquivos necessarios de acordo com parametro n
	 */
	public static String[] createFiles(int n, String filename) throws IOException {
		String[]arqs = new String[n];
		for (int i = 0; i < n; i++) { // cria os n arquivos
			RandomAccessFile output = new RandomAccessFile(filename + (i + 1) + "tmp.bin", "rw");
			arqs[i] = filename + (i + 1) + "tmp.bin";
			output.writeInt(0); // cabaço
			output.close();
		}
		return arqs;
	}

	/*
	 * Popula os arquivos criados com o arquivo inicial
	 */
	public static void populateSortFiles (RandomAccessFile input, String[] arqs, int m, int n, String filename, int maxId) throws IOException {
		byte ba[];
		int controle = 1;//usado para saber em qual arquivo vai ficar salvo
		int pos1 = 4;
		int pos2 = 4;
		
		input.seek(4);//pular o cabecalho com os ids do arquivo inicial

		
		while (input.getFilePointer() < input.length()) { // rodar o arquivo binario inteiro
			// System.out.println("????????????????");
			int tamanhos[] = new int[m];//salvar o tamanho de cada registro

			Movie[] filmes = new Movie[m];//array de filmes pro quicksort de tamanho "m” registros (parametrizável)

			for (int i = 0; i < m; i++) {
				//criar os objetos
				filmes[i] = new Movie(); 
				if (input.getFilePointer() < input.length()) {
					tamanhos[i] = input.readInt();
					ba = new byte[tamanhos[i]];
					input.read(ba);
					filmes[i].fromByteArray(ba);
				}
			}

			quickSort(filmes, 0, filmes.length - 1); // ordena os blocos de tamanho m atributos em memoria principal

			if (controle % n == 0) {
				 System.out.println("abc!");
				pos1 = distribuir(tamanhos, filmes, filename, n, pos1);
			} else {
				 System.out.println("abcde!?");
				pos2 = distribuir(tamanhos, filmes, filename, controle % n, pos2); // distribui os arquivos nos diferentes
																		// caminhos(arquivos)
																		// n%controle(n numero max)
			}
			
			controle++;
		}
	}
	
	/*
	 * Metodo para organizar os registros
	 */
	private static void quickSort(Movie[] vetor, int inicio, int fim) {
		if (inicio < fim) {
			int posicaoPivo = separar(vetor, inicio, fim);
			quickSort(vetor, inicio, posicaoPivo - 1);
			quickSort(vetor, posicaoPivo + 1, fim);
		}
	}

	/*
	 * Metodo para organizar os registros
	 */
	private static int separar(Movie[] vetor, int inicio, int fim) {
		Movie pivo = vetor[inicio];
		int i = inicio + 1, f = fim;
		while (i <= f) {
			if (vetor[i].id <= pivo.id)
				i++;
			else if (pivo.id < vetor[f].id)
				f--;
			else {
				Movie troca = vetor[i];
				vetor[i] = vetor[f];
				vetor[f] = troca;
				i++;
				f--;
			}
		}
		vetor[inicio] = vetor[f];
		vetor[f] = pivo;
		return f;
	}

	private static void intercalacaoBalanceada(String[] inputFiles, int m, int n) throws IOException {
		//variaveis usadas no geral
		CRUD crud = new CRUD("../data/arquivo.bin");
		int maxId = crud.getMaxId() + 1;
		int inputAmount = inputFiles.length - 2;

		//objetos filmes usados na ordenação externa
		Movie j_temp1 = new Movie();
		Movie j_temp2 = new Movie();

		//variaveis usadas para controlar o for
		int passadas = calcularPassadas(m, n);
		int regPorArq = (int) (Math.ceil((float) maxId / (float) m)/2)*m;//quantidade de registros em cada arquivo
		int diferenca = (int)(regPorArq*n - maxId);//diferenca que pode ter em arquivo
		int quantBloco = (int)Math.ceil((double)(regPorArq)/(double)m);//quantidade de blocos em cada arquivo
		//System.out.println("quant bloco antes = " + quantBloco);
		int interiorBloco = m;

		

		//variaveis usadas para fazer a ordenação externa
		byte ba1[];
		byte ba2[];
		int controle1, controle2 = 0, controle3 = 0;//usadas para saber qual arquivo abrir
		int pos1  = 4, pos2 = 4;//usadas para saber se mudou o registro que vai comparar
		int posOut1  = 4, posOut2 = 4;
		int tamanho = 0;
		int count1 = 0, count2 = 0;

		//for de cada fase
		for (int i = 0; i < passadas; i++) {
			posOut1 = posOut2 = 4;//resetar pra nova passada
			pos1 = pos2 = 4;//resetar pra nova passada
			//System.out.println("passadas = " + i);
			
			//for de cada bloco
			for (int k = 0; k < quantBloco; k++) {	
				//organizar em qual arquivo vai ler e em qual vai escrever
				if (i % 2 == 0) {
					controle1 = 0;
					controle2 = n;
				} else {
					controle1 = n;
					controle2 = 0;
				}
				System.out.println("qb = " + quantBloco);
				System.out.println("k = " + k);

				
				if (k%n != 0){
					controle2++;
					controle3 = 0;
				}

				
					System.out.println("raf1 = " + inputFiles[controle1    ]);
					System.out.println("raf2 = " + inputFiles[controle1  +1  ]);
					System.out.println("rafOut = " + inputFiles[controle2    ]);
				
				
				//for do interior de cada bloco	
				for (int j = 0; j < interiorBloco ; j++) {			

					//arquivo de saída e entrada
					RandomAccessFile raf1   = new RandomAccessFile(inputFiles[controle1    ], "r" );
					RandomAccessFile raf2   = new RandomAccessFile(inputFiles[controle1 + 1], "r" );
					RandomAccessFile rafOut = new RandomAccessFile(inputFiles[controle2    ], "rw");

					boolean condicao1 = (count1 < interiorBloco-1);
					boolean condicao2 = (count2 < interiorBloco-1);

					raf1.seek(0);
					int size1 = raf1.readInt();
					raf2.seek(0);
					int size2 = raf2.readInt();

					if ((k == quantBloco-1) && ((size1 + diferenca == regPorArq) || (size1 - diferenca == regPorArq))){
						//System.out.println("acaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " + size1);
						//System.out.println("interiorBloco = " + regPorArq);
						System.out.println("diferenca = " + diferenca);

						//System.out.println("interiorBloco = " + (Math.ceil((double)size1/(double)quantBloco)));
						condicao1 = (count1 < ((interiorBloco - 1) - (diferenca)) );
						//condicao2 = (count2 < interiorBloco-1);
					} else if ((k == quantBloco-1) && ((size2 + diferenca == regPorArq) || (size2 - diferenca == regPorArq))){
						//condicao1 = (count1 < interiorBloco-1);
						condicao2 = (count2 < ((interiorBloco - 1) - (diferenca)) );
					}

					raf1.seek(pos1);
					raf2.seek(pos2);

					//apontar corretamente pra posicao do rafOut
					if (k % 2 == 0) {
						rafOut.seek(posOut1);//se for par continua de onde parou anteriormente
					} else {
						rafOut.seek(posOut2);//se for impar continua de onde parou anteriormente
					}

					if (condicao1){//se o primeiro existir
						//lendo o registro do primeiro arquivo

						tamanho = raf1.readInt();
						ba1 = new byte[tamanho];					
						raf1.read(ba1);
						if (tamanho < 500){
						j_temp1.fromByteArray(ba1);//transformando em objeto
						}
						

						if (condicao2){//se o primeiro e segundo existirem
							tamanho = raf2.readInt();
							ba2 = new byte[tamanho];					
							raf2.read(ba2);
							j_temp2.fromByteArray(ba2);//transformando em objeto
							

							//comparar qual e' menor
							if (j_temp1.id < j_temp2.id){				 
								pos1 = writeOutput(pos1, j_temp1, rafOut);//escrever no arquivo de output
								controle3++;
								count1++;
							} else {
								pos2 = writeOutput(pos2, j_temp2, rafOut);//escrever no arquivo de output
								controle3++;
								count2++;
							}
						} else {//se o primeiro existir mas o segundo nao
							pos1 = writeOutput(pos1, j_temp1, rafOut);//escrever no arquivo de output 
							controle3++;
							count1++;
						}
						
					} else if (condicao2){//se o primeiro não existir mas o segundo existir
						tamanho = raf2.readInt();
						ba2 = new byte[tamanho];					
						raf2.read(ba2);
						j_temp2.fromByteArray(ba2);//transformando em objeto
						

						pos2 = writeOutput(pos2, j_temp2, rafOut);//escrever no arquivo de output
						controle3 ++;
						count2++;
					}

				if (k % 2 == 0) {
					posOut1 = (int)(rafOut.getFilePointer());//salvar na variavel 1 se for par
				} else {
					posOut2 = (int)(rafOut.getFilePointer());//salvar na variavel 2 se for impar
				}

				raf1.close();
				raf2.close();
				rafOut.close();
				
				}

				count1 = 0;
				count2 = 0;
				
			}//fim for de cada bloco

			
			interiorBloco = interiorBloco*n;//mudar o tamanho de cada bloco de acordo com a fase
			//System.out.println("interiorBloco = " + interiorBloco + "/// n = " + n);
			System.out.println("regporarq = " + regPorArq);
			quantBloco = (int)Math.ceil((double)regPorArq/(double)interiorBloco);//mudar a quantidade de blocos por arquivo de acordo com a fase
			System.out.println("quantBloco = " + quantBloco);

			System.out.println("arquivo escrito por ultimo = " + inputFiles[controle2]);
		}//fim for de cada fase
		crud.mostrarTudo(inputFiles[controle2], 4);
		//crud.mostrarTudo("../data/arquivo4tmp.bin", 0);

	}

	public static int writeOutput(int pos, Movie j_temp, RandomAccessFile rafOut) throws IOException{
		int posUltimo = (int)rafOut.getFilePointer();
		rafOut.seek(0);
		int size = rafOut.readInt();
		rafOut.seek(0);
		rafOut.writeInt(size + 1);
		rafOut.seek(posUltimo);

		byte[] ba = new byte[j_temp.toByteArray().length];
		ba = j_temp.toByteArray();

		rafOut.writeInt(ba.length);
		rafOut.write(ba);
		pos = pos + ba.length + 4;//atualizar pos pra nova posicao

		

		//System.out.println(j_temp);

		//crud.mostrarTudo(filename, 0);

		return (pos);
	}

	private static int calcularPassadas(int m, int n) throws IOException {
		CRUD crud = new CRUD("../data/arquivo.bin");
		double maxId = crud.getMaxId();
		crud.fechar();
		int passadas = (int) (1 + Math.ceil((Math.log((maxId / (double) m))) / Math.log((double) n)));

		return passadas;

	}

	private static int distribuir(int[] tamanhos, Movie[] filmes, String filename, int n, int pos) throws IOException {
		RandomAccessFile output = new RandomAccessFile(filename + n + "tmp.bin", "rw");
		byte ba[];
		if (output.length() == 0) {
			output.seek(0);
			output.writeInt(filmes.length); // cabaço
		}
		output.seek(pos);

		int count = 0;
		//System.out.println(pos);
		for (int i = 0; i < filmes.length; i++) {
			if (filmes[i].id != -1) {			
				ba = new byte[tamanhos[i]];
				ba = filmes[i].toByteArray();
				output.writeInt(ba.length);
				output.write(ba);
				count++;
			}
		}
		pos = (int)output.getFilePointer();
		output.seek(0);
		int tmp = output.readInt();
		output.seek(0);
		output.writeInt(tmp + count);
		System.out.println("tamanho arquivo " + (tmp+count));	
		output.close();
		return pos;
	}
}