package player;


import java.util.ArrayList;
import java.util.List;

import deck.*;
import engine.*;
/**
 * A classe <code>PlayerRA203609</code> representa uma classe de um jogador espec�fico (RA 203609) em Java.
 * A classe herda fun��es e atributos da superclasse Player.
 * A classe possui alguns atributos que auxiliam a implementa��o da estrat�gia. Todos s�o inicializados no
 * m�todo construtor. Mesmo que alguns s�o inicializados como null, algum valor diferente de null sempre
 * � atribu�do a a estes atributos antes de serem utilizados.
 * Uma estrutura de dados do tipo vetor din�mico � utilizada.
 * Alguns m�todos s�o utilizados somente uma vez: na primeira rodada. Depois disso, em cada rodada a carta escolhida
 * � decidida por v�rios outros m�todos que implementam a estrat�gia.
 * Na primeira rodada, meu baralho � definido e, por conseguinte, o baralho do advers�rio � constru�do espelhando-se
 * todas as cartas do meu baralho. Para isso, a seguinte ideia � utilizada: se tenho um trunfo
 * na m�o, o advers�rio recebe um carta trunfo correspondente; se n�o, um naipe sim�trico � adicionado
 * � m�o do advers�rio.
 * A partir disso, em cada rodada o baralho do advers�rio � atualizado utilizando informa��es da pilha.
 * A estrat�gia � dividida em duas partes: se sou o primeiro a jogar ou se sou o segundo.
 * Se sou o primeiro a jogar: procuro o naipe de menor ocorr�ncia na m�o do advers�rio que n�o � o trunfo dele nem meu
 * trunfo. Caso eu n�o possua esse naipe, procuro pelo pr�ximo naipe que atende a esses requisitos. Se ainda assim
 * eu n�o possuir esse naipe, um trunfo do advers�rio � jogado. Somente em �ltimo caso (caso eu n�o possua nenhum
 * outro naipe) jogo uma carta que � meu trunfo.
 * Se sou o segundo a jogar: procuro no meu baralho uma carta do naipe corrente que possui o menor Rank. Caso n�o
 * exista essa carta, verifico se � poss�vel jogar um trunfo. Se, depois destas tentativas, n�o for poss�vel jogar
 * nenhuma carta, ent�o pego todas as cartas da mesa.
 * 
 * @author Matheus V. Mazon
 *
 */
public class PlayerRA203609 extends Player {
	/**
	 * O atributo cardToPlay representa uma carta a ser jogada.
	 */
	public Card cardToPlay;
	/**
	 * O atributo pegarTudo representa uma jogada do tipo TAKEALLCARDS.
	 */
	public Play pegarTudo;
	/**
	 * O atributo jogarCarta representa uma jogada do tipo PLAYACARD.
	 */
	public Play jogarCarta;
	/**
	 * O atributo minhaMao � um vetor de cards que representa o meu baralho.
	 */
	public List<Card> minhaMao;
	/**
	 * O atributo maoAdversario � um vetor de cards que representa o baralho do advers�rio.
	 */
	public List<Card> maoAdversario;
	/**
	 * O atributo pilhaSizeAntigo representa o tamanho antigo da pilha de cartas jogadas (mesa).
	 */
	public int pilhaSizeAntigo;
	
	/**
	 * Construtor da classe Player. Inicializa dois atributos do jogador.
	 * @param trump o naipe trunfo
	 */
	public PlayerRA203609(Suit trump) {
		super(trump);
		cardToPlay = null;
		pegarTudo = new Play(PlayType.TAKEALLCARDS);
		jogarCarta = null;
		minhaMao = new ArrayList<Card>();
		maoAdversario = new ArrayList<Card>();
		pilhaSizeAntigo = 0;
		// TODO Auto-generated constructor stub
	}

	/**
	 * Recupera o naipe trunfo do jogador
	 */
	@Override
	public Suit getTrump() {
		// TODO Auto-generated method stub
		return this.trump;
	}
	
	/**
	 * Joga uma rodada do jogo de cartas.
	 * @param arg0 valor boolean indicando true se o jogador for o primeiro e false caso contr�rio.
	 * @param arg1 objeto para acessar m�todos da engine.
	 */
	@Override
	public Play playRound(boolean arg0, Engine arg1) {
		
		if(arg1.getCurrentRound() == 1) {
			minhaMao = arg1.getUnmodifiableHandOfPlayer(this); // inicializa meu baralho
			maoAdversario = verMaoAdversarioInicial(minhaMao, arg1); // inicializa o baralho do advers�rio
		}
		
		if(this.getTrump() == arg1.getPlayer1Trump()) { // se sou o jogador 1
			cardToPlay = decidirCard(arg0, arg1, minhaMao, maoAdversario, arg1.getPlayer2Trump());
		}
		else { // se sou o jogador 2
			cardToPlay = decidirCard(arg0, arg1, minhaMao, maoAdversario, arg1.getPlayer1Trump());
		}
		
		if(cardToPlay == null) {
			return pegarTudo;
		}
		
		jogarCarta = new Play(PlayType.PLAYACARD, cardToPlay);
		return jogarCarta;
		
		// TODO Auto-generated method stub
	}
	
	
	/**
	 * Atualiza a m�o do advers�rio e decide qual a melhor carta se for o primeiro ou o segundo a jogar
	 * ou retorna null, pegando todas as cartas.
	 * @param arg0 booleano indicando true se for o primeiro a jogar e false caso contr�rio.
	 * @param arg1 objeto da Engine.
	 * @param minhaMao meu baralho.
	 * @param maoAdversario baralho advers�rio
	 * @param advTrump trunfo do advers�rio
	 * @return a melhor carta a jogar ou null.
	 */
	public Card decidirCard(boolean arg0, Engine arg1, List<Card> minhaMao, List<Card> maoAdversario, Suit advTrump) {
		
		if(arg1.getUnmodifiablePlays().isEmpty() == false) // se h� cartas na pilha, atualiza a m�o do advers�rio
			maoAdversario = attMaoAdversario(arg0, arg1, maoAdversario);
		
		if(arg0 == true) // se eu sou o primeiro
			return searchMinimalCardFirst(minhaMao, maoAdversario, advTrump);
		
		// se eu sou o segundo
		return searchBetterCard(minhaMao, arg1);
	}
	
	/**
	 * Procura a menor carta para ser jogada baseada no meu baralho e no do advers�rio.
	 * @param minhaMao meu baralho
	 * @param advMao baralho do advers�rio.
	 * @param advTrump trunfo fo advers�rio.
	 * @return a menor carta a ser jogada.
	 */
	public Card searchMinimalCardFirst(List<Card> minhaMao, List<Card> advMao, Suit advTrump) {
		
		Card card;
		Suit suit;
		
		suit = lessSuit(advMao, advTrump); // naipe que o advers�rio menos tem
		if(searchSuit(minhaMao, suit) == true) { // se tenho o naipe
			card = searchMinimalRank(minhaMao, suit); // procura a menor
			if(card != null)
				return card;
		}
		
		for(int i=0; i < minhaMao.size(); i++) { // testa o outro naipe
			if(minhaMao.get(i).getSuit() != getTrump() && minhaMao.get(i).getSuit() != advTrump && minhaMao.get(i).getSuit() != suit) {
				card = searchMinimalRank(minhaMao, minhaMao.get(i).getSuit());
				if(card != null)
					return card; // the first card that isn't neither my trump nor his trump
			}
		}
		
		card = searchMinimalRank(minhaMao, advTrump); // retorna o menor trunfo do aversario
		if(card != null) 
			return card;
		
		return searchMinimalRank(minhaMao, getTrump()); // retorna meu menor trunfo
	}
	
	/**
	 * Procura no vetor alguma carta do naipe passado por par�metro.
	 * @param vetor vetor de cartas.
	 * @param naipe naipe a ser procurado.
	 * @return true se encontrou e false caso contr�rio.
	 */
	public static boolean searchSuit(List<Card> vetor, Suit naipe) {
		
		for(int i=0; i < vetor.size(); i++) {
			if(vetor.get(i).getSuit() == naipe)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Procura no meu baralho a menor carta do naipe passado por par�metro.
	 * @param minhaMao meu baralho.
	 * @param naipe naipe a ser buscado.
	 * @return a carta que possui o menor rank do respectivo naipe.
	 */
	public static Card searchMinimalRank(List<Card> minhaMao, Suit naipe) {
		
		Card menor = new Card(naipe, Rank.ACE);
		boolean trocou = false;
		
		for(int i=0; i < minhaMao.size(); i++) {
			if(minhaMao.get(i).getSuit() == naipe) {
				if(minhaMao.get(i).compareTo(menor) <= 0) { // procura o menor rank
					trocou = true;
					menor = minhaMao.get(i);
				}
			}
		}
		
		if(trocou == true) {
			return menor;
		}
		
		return null;
	}
	
	/**
	 * Procura o naipe de menor ocorr�ncia em um vetor de cards.
	 * @param vetor o vetor de cards.
	 * @param advTrump o naipe do avers�rio.
	 * @return o naipe de menor ocorr�ncia.
	 */
	public Suit lessSuit(List<Card> vetor, Suit advTrump) { // verifica o naipe com menor ocorrencia no vetor
		
		List<Integer> oc = new ArrayList<Integer>(); // vetor de ocorr�ncia dos naipes
		int clovers, pikes, hearts, tiles; // representa��o dos naipes
		clovers = pikes = hearts = tiles = 0;
		
		for(int i=0; i < vetor.size(); i++) {
			if(vetor.get(i).getSuit() != getTrump() && vetor.get(i).getSuit() != advTrump) {
				if(vetor.get(i).getSuit() == Suit.CLOVERS)
					clovers++;
				else if(vetor.get(i).getSuit() == Suit.PIKES)
					pikes++;
				else if(vetor.get(i).getSuit() == Suit.HEARTS)
					hearts++;
				else
					tiles++;
			}
		}
		
		oc.add(clovers);
		oc.add(pikes);
		oc.add(hearts);
		oc.add(tiles);
		
		oc.sort(null); // o naipe de menor ocorr�ncia estar� na primeira posi��o do vetor
		
		for(int i=0; i < oc.size(); i++) {
			if(oc.get(i) == clovers && (Suit.CLOVERS != getTrump() && Suit.CLOVERS != advTrump))
				return Suit.CLOVERS;
			else if(oc.get(i) == pikes && (Suit.PIKES != getTrump() && Suit.PIKES != advTrump))
				return Suit.PIKES;
			else if(oc.get(i) == hearts && (Suit.HEARTS != getTrump() && Suit.HEARTS != advTrump))
				return Suit.HEARTS;
			else if(oc.get(i) == tiles && (Suit.TILES != getTrump() && Suit.TILES != advTrump))
				return Suit.TILES;
		}
	
		return null;
	}
	
	/**
	 * M�todo utilizado se sou o segundo a jogar na rodada. De todas as minhas melhores cartas, procura a menor.
	 * Se n�o houver, verifica se � poss�vel jogar um trunfo. Se tamb�m n�o for poss�vel, pega todas as cartas da mesa.
	 * @param minhaMao meu baralho.
	 * @param arg1 objeto da Engine.
	 * @return a melhor carta a se jogar.
	 */
	public Card searchBetterCard(List<Card> minhaMao, Engine arg1) {
		
		List<Card> myRanks = new ArrayList<Card>();
		List<Card> myTrumps = new ArrayList<Card>();
		Card card;
		Card lastCard = arg1.peekCardsOnTable();

		for(int i=0; i < minhaMao.size(); i++) { // se tenho uma carta melhor
			if(minhaMao.get(i).getSuit() == lastCard.getSuit() && minhaMao.get(i).compareTo(lastCard) > 0) {
				myRanks.add(minhaMao.get(i));
			}
		}
		
		card = searchMinimalRank(myRanks, lastCard.getSuit()); // procuro a menor
		if(card != null)
			return card;
		
		for(int i=0; i < minhaMao.size(); i++) { // se s� tenho o trunfo como melhor
			if(minhaMao.get(i).getSuit() == getTrump() && getTrump() != lastCard.getSuit())
				myTrumps.add(minhaMao.get(i));
		}
		
		card = searchMinimalRank(myTrumps, getTrump()); // procuro o menor trunfo
		if(card != null)
			return card;
		
		return null; // se n�o tenho nada, pego todas as cartas
	}
	
	/**
	 * Atualiza a m�o do advers�rio a cada rodada.
	 * @param arg0 valor booleano indicando se sou o primeiro a jogar.
	 * @param arg1 objeto da Engine.
	 * @param maoAdv baralho do advers�rio.
	 * @return a m�o do advers�rio.
	 */
	public List<Card> attMaoAdversario(boolean arg0, Engine arg1, List<Card> maoAdv){
		
		List<Play> pilha = arg1.getUnmodifiablePlays();
		// se o advers�rio pegou todas as cartas da pilha
		if(pilha.get(pilha.size() - 1).getType() == PlayType.TAKEALLCARDS && arg0 == true) {
			for(int i=pilhaSizeAntigo; i < pilha.size(); i++) {
				if(pilha.get(i).getCard() != null)
					maoAdv.add(pilha.get(i).getCard());
			}
		}
		// se s� jogou as cartas na pilha
		else {
			for(int i=pilhaSizeAntigo; i < pilha.size(); i++) {
				for(int j=0; j < maoAdv.size(); j++) {
					if(pilha.get(i).getCard() == maoAdv.get(j)) {
						maoAdv.remove(j);
					}
				}
			}
		}
		
		pilhaSizeAntigo = pilha.size(); // atualiza o valor antigo
		
		return maoAdv;
	}

	/**
	 * Constr�i o baralho do advers�rio espelhando todas as minhas cartas.
	 * @param minhaMao meu baralho.
	 * @param arg1 objeto da Engine.
	 * @return o baralho do advers�rio.
	 */
	public List<Card> verMaoAdversarioInicial(List<Card> minhaMao, Engine arg1) {
		
		List<Card> maoAdversario = new ArrayList<Card>();
		Suit myTrump = this.getTrump();
		Suit suitAdd;
		Rank rankAdd;
		Card advAdd;
		
		if(myTrump == arg1.getPlayer1Trump()) { // se sou o player 1
			
			for(int i=0; i < minhaMao.size(); i++) {
				
				if(minhaMao.get(i).getSuit() == myTrump) { // se tenho trunfo
					rankAdd = minhaMao.get(i).getRank();
					advAdd = new Card(arg1.getPlayer2Trump(), rankAdd); // adiciona um trunfo para o advers�rio
					maoAdversario.add(advAdd);
				}
				
				else { // se n�o tenho um trunfo
					suitAdd = getAdvSuit(minhaMao.get(i).getSuit(), arg1.getPlayer2Trump()); // procura o naipe
					rankAdd = minhaMao.get(i).getRank();
					advAdd = new Card(suitAdd, rankAdd);
					maoAdversario.add(advAdd);
				}
			}
		}
		
		else {	// se sou o player 2. A implementa��o � sim�trica
			for(int i=0; i < minhaMao.size(); i++) {
				
				if(minhaMao.get(i).getSuit() == myTrump) { // if I have a trump
					rankAdd = minhaMao.get(i).getRank();
					advAdd = new Card(arg1.getPlayer1Trump(), rankAdd); // add a trump to the other player
					maoAdversario.add(advAdd);
				}
				
				else { // if I don't have a trump
					suitAdd = getAdvSuit(minhaMao.get(i).getSuit(), arg1.getPlayer1Trump());
					rankAdd = minhaMao.get(i).getRank();
					advAdd = new Card(suitAdd, rankAdd);
					maoAdversario.add(advAdd);
				}
			}
		}
		
		return maoAdversario;
	}
	
	// Adv Suit
	/**
	 * Verifica qual � o naipe do advers�rio que n�o � meu trunfo nem o dele.
	 * Essa fun��o considera que o naipe corrente n�o � meu trunfo. O caso em que o naipe corrente � o trunfo
	 * do advers�rio � tratado no in�cio da fun��o.
	 * @param mySuit naipe da carta corrente que tenho na m�o.
	 * @param hisTrump trunfo do advers�rio.
	 * @return o naipe.
	 */
	public Suit getAdvSuit(Suit mySuit, Suit hisTrump) {
		
		if(mySuit == hisTrump) { // se minha carta � o trunfo do oponente
			return getTrump();
		}
		
		if(getTrump().getColor() == Color.BLACK) {	// se meu trunfo � preto
			if(mySuit.getColor() == Color.BLACK) { // se minha carta � preta
				if(hisTrump == Suit.HEARTS) { // retorna uma carta que n�o � o trunfo do oponente
					return Suit.TILES;
				}
				else {
					return Suit.HEARTS;
				}
			}
			else { // se minha carta � vermelha
				if(getTrump() == Suit.CLOVERS)
					return Suit.PIKES;
				else
					return Suit.CLOVERS;
			}
		}
		
		else { // se meu trunfo � vermelho
			if(mySuit.getColor() == Color.RED) { // se minha carta � vermelha
				if(hisTrump == Suit.CLOVERS) { // devolve um n�o trunfo do oponente
					return Suit.PIKES;
				}
				else {
					return Suit.CLOVERS;
				}
			}
			else{ // se minha carta � preta
				if(getTrump() == Suit.HEARTS) // devolve um n�o trunfo do oponente
					return Suit.TILES;
				else
					return Suit.HEARTS;
			}
		}
	}
	
	
}
