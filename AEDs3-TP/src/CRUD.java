import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class CRUD {
	private RandomAccessFile file;

	public CRUD(String nomeArquivo) throws IOException {
		String tmp = "../data/" + nomeArquivo;
		this.file = new RandomAccessFile(tmp, "rw");
	}

	public void fechar() throws IOException {
		file.close();
	}

	public int getMaxId() throws IOException {
		file.seek(0);
		return (file.readInt());
	}

	public void inserir(byte[] ba) throws IOException {
		// variaveis que vao ser usadas para posicionar e id
		int posicao = 4;
		int tmp = 0;
		int tamanho = 0;

		// criar o objeto que vai ser inserido
		Movie j_temp = new Movie();
		j_temp.fromByteArray(ba);

		// percorrer o arquivo todo cacando um espaco
		while (posicao < file.length() || posicao < 0) {

			file.seek(posicao);
			tamanho = file.readInt();

			boolean lapide = file.readBoolean();

			if (lapide == true && tamanho >= ba.length) {

				j_temp.id = tmp;

				file.seek(posicao + 4); // pular o tamanho

				byte[] arr = j_temp.toByteArray();
				file.write(arr);

				System.out.println("filme adicionado na posicao: " + j_temp.id);
				return;
			}

			posicao += tamanho + 4;

			tmp++;
		}

		j_temp.id = getMaxId() + 1;

		file.seek(posicao);
		file.writeInt(ba.length);// escrever o tamanho

		byte[] arr = j_temp.toByteArray();
		file.write(arr);
		file.seek(0);
		file.writeInt(tmp);

		System.out.println("filme adicionado na posicao: " + j_temp.id);
		return;

	}

	public Movie buscar(int id) throws IOException {
		int posicao = apontar(id);
		int tamanho;

		byte ba[];
		Movie j_temp = new Movie();

		if (posicao != -1) {
			tamanho = file.readInt();
			ba = new byte[tamanho];
			file.seek(posicao + 4);
			file.read(ba);
			j_temp.fromByteArray(ba);
			return j_temp;
		}
		return null;
	}

	public Movie buscar(String title) throws IOException {
		int posicao = apontar(title);
		int tamanho;

		byte ba[];
		Movie j_temp = new Movie();

		if (posicao != -1) {
			tamanho = file.readInt();
			ba = new byte[tamanho];
			file.seek(posicao + 4);
			file.read(ba);
			j_temp.fromByteArray(ba);
			return j_temp;
		}
		return null;
	}

	public void atualizar(int id) throws IOException {
		Scanner sc = new Scanner(System.in);
		int posicao = apontar(id);
		int temp = 0;
		int temp2 = 0;
		String tmp = "";

		// Percorre o arquivo em busca do movie com o ID especificado
		Movie j_temp = new Movie();
		j_temp = buscar(id);

		System.out.println("qual atributo do filme voce deseja atualizar?");

		while (true) {
			System.out.println("Selecione um atributo:");
			System.out.println("1 - T??tulo = " + j_temp.title);
			System.out.println("2 - Diretor = " + j_temp.director);
			System.out.println("3 - Certificado = " + j_temp.certificate);
			System.out.println("4 - Genero(s) = " + Arrays.toString(j_temp.genre));
			System.out.println("5 - Nota = " + j_temp.rating);
			System.out.println("6 - Ano Lan??amento = " + j_temp.year);
			System.out.println("0 - Sair");

			int opcao = sc.nextInt();
			sc.nextLine(); // limpa o buffer do scanner
			switch (opcao) {

				case 1:
					System.out.println("Novo t??tulo: ");
					tmp = sc.nextLine();

					j_temp.title = tmp;

					break;

				case 2:
					System.out.println("Novo diretor: ");
					tmp = sc.nextLine();

					j_temp.director = tmp;

					break;

				case 3:
					System.out.println("Escolha o certificado de classifica????o et??ria para o filme:");
					System.out.println("1 - A (all ages)");
					System.out.println("2 - PG-13");
					System.out.println("3 - R");
					System.out.println("4 - U");
					System.out.println("5 - UA");

					int escolha = sc.nextInt();
					switch (escolha) {
						case 1:
							tmp = "A";
							break;
						case 2:
							tmp = "PG-13";
							break;
						case 3:
							tmp = "R";
							break;
						case 4:
							tmp = "U";
							break;
						case 5:
							tmp = "UA";
							break;
						default:
							System.out.println("Op????o inv??lida!");
							// break;
					}

					j_temp.certificate = tmp;

					break;

				case 4:
					// ler os generos
					System.out.println("Digite quantos g??neros o filmes vai ter");
					int k = sc.nextInt();
					int size = 0;

					String[] genre = new String[k];
					sc.nextLine();
					for (int i = 0; i < k; i++) {
						System.out.println("Digite o " + (i + 1) + "?? g??nero + ENTER");
						genre[i] = sc.nextLine();
						size += genre[i].length();
					}

					// verificar se cabe
					file.seek(posicao + 9 + j_temp.title.length() + j_temp.director.length()
							+ j_temp.certificate.length());

					j_temp.genre = genre;

					break;

				case 5:
					float rating;
					System.out.println("Digite a avalia????o do filme, separado por v??rgula");
					rating = sc.nextFloat();
					/*
					 * file.seek(posicao + 9 + j_temp.title.length() + j_temp.director.length() +
					 * j_temp.certificate.length()); temp = file.readInt();// pegar o tamanho do
					 * array de generos temp2 = 0; for (int i = 0; 0 < temp; i++) { temp2 +=
					 * file.readUTF().length(); }
					 * 
					 * file.seek(posicao + 9 + j_temp.title.length() + j_temp.director.length() +
					 * j_temp.certificate.length() + 4 + temp2);
					 * 
					 * j_temp.rating = rating; file.writeFloat(rating);
					 */
					j_temp.rating = rating;
					break;

				case 6:
					System.out.println("Digite o ano de lan??amento do filme");
					int date = sc.nextInt();

					Date year = new Date();// por enquanto deixei assim(atributos[1])
					/*
					 * file.seek(posicao + 9 + j_temp.title.length() + j_temp.director.length() +
					 * j_temp.certificate.length());
					 * 
					 * temp = file.readInt();// pegar o tamanho do array de generos temp2 = 0; for
					 * (int i = 0; 0 < temp; i++) { temp2 += file.readUTF().length(); }
					 * 
					 * file.seek(posicao + 9 + j_temp.title.length() + j_temp.director.length() +
					 * j_temp.certificate.length() + 4 + temp2); file.writeLong(year.getTime());
					 */
					j_temp.year = year;
					break;

				case 0:
					remover(id);
					inserir(j_temp.toByteArray());
					System.out.println("Saindo...");
					return;
				default:
					System.out.println("Op????o inv??lida!");
			}
		}

	}

	public Movie remover(int id) throws IOException {
		// Percorre o arquivo em busca do movie com o ID especificado
		int posicao = apontar(id);
		Movie j_temp = new Movie();
		if (posicao != -1) {
			byte ba[];
			file.seek(posicao);
			int tamanho = file.readInt();

			ba = new byte[tamanho];
			file.read(ba);

			file.seek(posicao + 4);
			file.writeBoolean(true); // marca o movie como removido

			j_temp.fromByteArray(ba);

			System.out.println("ITEM REMOVIDO!");
		} else {
			System.out.println("ERRO: ITEM N??O ENCONTRADO");
		}
		return j_temp;
	}

	/*
	 * int apontar - aponta pro inicio do registro
	 * 
	 * @param int id - id do objeto procurado
	 * 
	 * @return int - false = -1 true = posicao
	 */
	public int apontar(int id) throws IOException {
		// Percorre o arquivo em busca do movie com o ID especificado
		int posicao = 4;
		int len;
		while (posicao < file.length()) {
			file.seek(posicao);
			int tamanho = file.readInt();
			boolean lapide = file.readBoolean();
			file.seek(posicao + 5);
			int registroId = file.readInt();
			if (lapide == false && registroId == id) {
				return posicao;
			}
			posicao += tamanho + 4;
		}
		return -1;
	}

	public int apontar(String title) throws IOException {
		// Percorre o arquivo em busca do movie com o ID especificado
		int posicao = 4;
		int len;
		String registroTitle = "";
		while (posicao < file.length()) {
			file.seek(posicao);
			int tamanho = file.readInt();
			boolean lapide = file.readBoolean();
			file.seek(posicao + 5);
			file.readInt();
			registroTitle = file.readUTF();
			if (lapide == false && registroTitle.equals(title)) {
				return posicao;
			}
			posicao += tamanho + 4;
		}
		return -1;
	}

	public static List<Movie> readCsv(String filename) {
		List<Movie> filmes = new ArrayList<>();
		int id = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			br.readLine(); // Ignora a primeira linha que cont??m cabe??alhos de coluna
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				String[] atributos = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				// o regex acima divide a linha em campos, ignorando as v??rgulas entre aspas
				String title = atributos[0];
				int year = 0;
				if (atributos[1].length() == 4) {
					year = Integer.parseInt(atributos[1]);
				} else {
					year = Integer.parseInt(atributos[1].substring(atributos[1].length() - 4));
				}
				String certificate = atributos[2];
				String[] genre = atributos[3].replaceAll("^\"|\"$", "").split(",");
				float rating = Float.parseFloat(atributos[4]);
				String director = atributos[5];
				Movie filme = new Movie(false, id, title, year, certificate, genre, rating, director);
				id++;
				filmes.add(filme);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filmes;
	}

	public void cargaInicial() {
		// RandomAccessFile fos = new
		// RandomAccessFile("/home/gabriel/git/AEDs3-TP/AEDs3-TP/src/movies.csv", rw");
		List<Movie> filmes = readCsv("../data/movies.csv");
		byte ba[];
		try {
			if (file.length() == 0) {
				file.seek(0);
				file.writeInt(filmes.size() - 1); // caba??o
			} else {
				file.seek(4);
			}
			for (int i = 0; i < filmes.size(); i++) {
				// System.out.println("Posicao do registro: " + fos.getFilePointer());
				ba = filmes.get(i).toByteArray();
				if (i < 10) {
					// System.out.println(ba.length);
				}
				file.writeInt(ba.length); // escreve tamanho da entidade
				file.write(ba); // escreve o byte de arrays da entidade
				// inserir(ba);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	/*
	 * public Movie buscar(int id) throws IOException { // Percorre o arquivo em
	 * busca do movie com o ID especificado int posicao = 4; int len; byte ba[];
	 * Movie j_temp = new Movie(); while (posicao < file.length()) {
	 * file.seek(posicao); int tamanho = file.readInt(); //
	 * System.out.println(posicao); // ba = new byte[tamanho]; // file.read(ba); //
	 * file.seek(posicao + tamanho + 4); // j_temp.fromByteArray(ba); //
	 * System.out.println(j_temp); boolean lapide = file.readBoolean();
	 * file.seek(posicao + 4); int registroId = file.readInt(); if (lapide == false
	 * && registroId == id) { file.seek(posicao + 4); ba = new byte[tamanho];
	 * file.read(ba); j_temp.fromByteArray(ba); return j_temp; } posicao += tamanho
	 * + 4; } return null; }
	 */

	/*
	 * public void atualizar(int id) throws IOException { Scanner sc = new
	 * Scanner(System.in); int posicao = apontar(id); int temp = 0; int temp2 = 0;
	 * String tmp = "";
	 * 
	 * // Percorre o arquivo em busca do movie com o ID especificado Movie j_temp =
	 * new Movie(); j_temp = buscar(id);
	 * 
	 * System.out.println("qual atributo do filme voce deseja atualizar?");
	 * 
	 * while (true) { System.out.println("Selecione um atributo:");
	 * System.out.println("1 - T??tulo = " + j_temp.title);
	 * System.out.println("2 - Diretor = " + j_temp.director);
	 * System.out.println("3 - Certificado = " + j_temp.certificate);
	 * System.out.println("4 - Genero(s) = " + Arrays.toString(j_temp.genre));
	 * System.out.println("5 - Nota = " + j_temp.rating);
	 * System.out.println("6 - Ano Lan??amento = " + j_temp.year);
	 * System.out.println("0 - Sair");
	 * 
	 * int opcao = sc.nextInt(); sc.nextLine(); // limpa o buffer do scanner switch
	 * (opcao) {
	 * 
	 * case 1: System.out.println("Novo t??tulo: "); tmp = sc.nextLine();
	 * 
	 * // verificar se cabe if (j_temp.title.length() > tmp.length()) { temp2 =
	 * tmp.length(); for (temp = 0; temp < j_temp.title.length() - temp2; temp++) {
	 * tmp += " "; } j_temp.title = tmp; file.seek(posicao + 8); file.writeUTF(tmp);
	 * buscar(10061); System.out.println(j_temp); } else { remover(id); j_temp.title
	 * = tmp; inserir(j_temp.toByteArray()); } break;
	 * 
	 * case 2: System.out.println("Novo diretor: "); tmp = sc.nextLine();
	 * 
	 * // verificar se cabe if (j_temp.director.length() > tmp.length()) { temp2 =
	 * tmp.length(); for (temp = 0; temp < j_temp.title.length() - temp2; temp++) {
	 * tmp += " "; } j_temp.director = tmp; file.seek(posicao + 9 +
	 * j_temp.title.length()); file.writeUTF(tmp); } else { remover(id);
	 * j_temp.director = tmp; inserir(j_temp.toByteArray()); } break;
	 * 
	 * case 3: System.out.
	 * println("Escolha o certificado de classifica????o et??ria para o filme:");
	 * System.out.println("1 - A (all ages)"); System.out.println("2 - PG-13");
	 * System.out.println("3 - R"); System.out.println("4 - U");
	 * System.out.println("5 - UA");
	 * 
	 * int escolha = sc.nextInt(); switch (escolha) { case 1: tmp = "A"; break; case
	 * 2: tmp = "PG-13"; break; case 3: tmp = "R"; break; case 4: tmp = "U"; break;
	 * case 5: tmp = "UA"; break; default: System.out.println("Op????o inv??lida!"); //
	 * break; }
	 * 
	 * // verificar se cabe if (j_temp.certificate.length() > tmp.length()) {
	 * j_temp.certificate = tmp; file.seek(posicao + 9 + j_temp.title.length() +
	 * j_temp.director.length()); temp2 = tmp.length(); for (temp = 0; temp <
	 * j_temp.title.length() - temp2; temp++) { tmp += " "; } file.writeUTF(tmp); }
	 * else { remover(id); j_temp.certificate = tmp; inserir(j_temp.toByteArray());
	 * } break;
	 * 
	 * case 4: // ler os generos
	 * System.out.println("Digite quantos g??neros o filmes vai ter"); int k =
	 * sc.nextInt(); int size = 0;
	 * 
	 * String[] genre = new String[k]; sc.nextLine(); for (int i = 0; i < k; i++) {
	 * System.out.println("Digite o " + (i + 1) + "?? g??nero + ENTER"); genre[i] =
	 * sc.nextLine(); size += genre[i].length(); }
	 * 
	 * // verificar se cabe file.seek(posicao + 9 + j_temp.title.length() +
	 * j_temp.director.length() + j_temp.certificate.length());
	 * 
	 * temp = file.readInt();// pegar o tamanho do array de generos
	 * 
	 * temp2 = 0; for (int i = 0; 0 < temp; i++) { temp2 += file.readUTF().length();
	 * } if (temp2 > size) {
	 * 
	 * temp2 = tmp.length(); for (temp = 0; temp < j_temp.title.length() - temp2;
	 * temp++) { tmp += " "; }
	 * 
	 * j_temp.genre = genre; // posicionar no local dos generos file.seek(posicao +
	 * 9 + j_temp.title.length() + j_temp.director.length() +
	 * j_temp.certificate.length());
	 * 
	 * // escrever a quantidade de generos que vai ser inserida
	 * file.writeInt(genre.length); String stringzona = ""; for (int i = 0; i <
	 * genre.length; i++) { stringzona += genre[i]; stringzona += ","; }
	 * file.writeUTF(stringzona); } else { remover(id); j_temp.genre = genre;
	 * inserir(j_temp.toByteArray()); } break;
	 * 
	 * case 5: float rating;
	 * System.out.println("Digite a avalia????o do filme, separado por v??rgula");
	 * rating = sc.nextFloat();
	 * 
	 * file.seek(posicao + 9 + j_temp.title.length() + j_temp.director.length() +
	 * j_temp.certificate.length());
	 * 
	 * temp = file.readInt();// pegar o tamanho do array de generos temp2 = 0; for
	 * (int i = 0; 0 < temp; i++) { temp2 += file.readUTF().length(); }
	 * 
	 * file.seek(posicao + 9 + j_temp.title.length() + j_temp.director.length() +
	 * j_temp.certificate.length() + 4 + temp2);
	 * 
	 * j_temp.rating = rating; file.writeFloat(rating);
	 * 
	 * break;
	 * 
	 * case 6: System.out.println("Digite o ano de lan??amento do filme"); int date =
	 * sc.nextInt();
	 * 
	 * Date year = new Date();// por enquanto deixei assim(atributos[1])
	 * 
	 * file.seek(posicao + 9 + j_temp.title.length() + j_temp.director.length() +
	 * j_temp.certificate.length());
	 * 
	 * temp = file.readInt();// pegar o tamanho do array de generos temp2 = 0; for
	 * (int i = 0; 0 < temp; i++) { temp2 += file.readUTF().length(); }
	 * 
	 * file.seek(posicao + 9 + j_temp.title.length() + j_temp.director.length() +
	 * j_temp.certificate.length() + 4 + temp2);
	 * 
	 * file.writeLong(year.getTime()); break;
	 * 
	 * case 0: System.out.println("Saindo..."); return; default:
	 * System.out.println("Op????o inv??lida!"); } }
	 * 
	 * }
	 */

}