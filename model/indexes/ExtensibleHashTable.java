// Caminho: model/indexes/ExtensibleHashTable.java (VERSÃO FINAL CORRIGIDA)
package model.indexes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.lang.reflect.Constructor;

public class ExtensibleHashTable<T extends GenericExtensibleHashTable> {

  String nomeArquivoDiretorio;
  String nomeArquivoCestos;
  RandomAccessFile arqDiretorio;
  RandomAccessFile arqCestos;
  int quantidadeDadosPorCesto;
  Diretorio diretorio;
  Constructor<T> construtor;

  public class Cesto {

    Constructor<T> construtor;
    short quantidadeMaxima;
    short bytesPorElemento;
    short bytesPorCesto;
    byte profundidadeLocal;
    short quantidade;
    ArrayList<T> elementos;

    public Cesto(Constructor<T> ct, int qtdmax) throws Exception { this(ct, qtdmax, 0); }
    public Cesto(Constructor<T> ct, int qtdmax, int pl) throws Exception {
      construtor = ct;
      if (qtdmax > 32767) throw new Exception("Quantidade máxima de 32.767 elementos");
      if (pl > 127) throw new Exception("Profundidade local máxima de 127 bits");
      profundidadeLocal = (byte) pl;
      quantidade = 0;
      quantidadeMaxima = (short) qtdmax;
      elementos = new ArrayList<>(quantidadeMaxima);
      bytesPorElemento = ct.newInstance().size();
      bytesPorCesto = (short) (bytesPorElemento * quantidadeMaxima + 3);
    }

    public byte[] toByteArray() throws Exception {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      dos.writeByte(profundidadeLocal);
      dos.writeShort(quantidade);
      for (int i = 0; i < quantidade; i++) {
        dos.write(elementos.get(i).toByteArray());
      }
      byte[] vazio = new byte[bytesPorElemento];
      for (int i = quantidade; i < quantidadeMaxima; i++) {
        dos.write(vazio);
      }
      return baos.toByteArray();
    }

       public void fromByteArray(byte[] ba) throws Exception {
            ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            DataInputStream dis = new DataInputStream(bais);
            profundidadeLocal = dis.readByte();
            quantidade = dis.readShort();
            elementos = new ArrayList<>(quantidadeMaxima);
            byte[] dados = new byte[bytesPorElemento];
            
            // O BUG ESTAVA AQUI: O loop agora itera apenas sobre os elementos existentes ('quantidade')
            for (int i = 0; i < quantidade; i++) {
                dis.readFully(dados); // Usar readFully é mais seguro que read
                T elem = construtor.newInstance();
                elem.fromByteArray(dados);
                elementos.add(elem);
            }
        }

    public boolean create(T elem) {
      if (full()) return false;
      int i = quantidade - 1;
      while (i >= 0 && elem.hashCode() < elementos.get(i).hashCode()) i--;
      elementos.add(i + 1, elem);
      quantidade++;
      return true;
    }

    public T read(int hashElem) {
      if (empty()) return null;
      int i = 0;
      while (i < quantidade && hashElem > elementos.get(i).hashCode()) i++;
      if (i < quantidade && hashElem == elementos.get(i).hashCode())
        return elementos.get(i);
      else
        return null;
    }

    public boolean update(T elem) {
      if (empty()) return false;
      int i = 0;
      while (i < quantidade && elem.hashCode() > elementos.get(i).hashCode()) i++;
      if (i < quantidade && elem.hashCode() == elementos.get(i).hashCode()) {
        elementos.set(i, elem);
        return true;
      }
      return false;
    }

    public boolean delete(int hashElem) {
      if (empty()) return false;
      int i = 0;
      while (i < quantidade && hashElem > elementos.get(i).hashCode()) i++;
      // BUG CORRIGIDO: Verificar se 'i' está dentro dos limites antes de acessar
      if (i < quantidade && hashElem == elementos.get(i).hashCode()) {
        elementos.remove(i);
        quantidade--;
        return true;
      }
      return false;
    }

    public boolean empty() { return quantidade == 0; }
    public boolean full() { return quantidade == quantidadeMaxima; }
    public String toString() {
      String s = "PL: " + profundidadeLocal + ", QTD: " + quantidade + " | ";
      for (int i = 0; i < quantidade; i++) s += elementos.get(i).toString() + " | ";
      for (int i = quantidade; i < quantidadeMaxima; i++) s += "- | ";
      return s;
    }
    public int size() { return bytesPorCesto; }
  }

  // O restante do arquivo (classe Diretorio e os métodos principais) não precisa de alteração,
  // mas está aqui para ser um arquivo completo.
    protected class Diretorio {
    byte profundidadeGlobal;
    long[] enderecos;
    public Diretorio() { profundidadeGlobal = 0; enderecos = new long[1]; enderecos[0] = 0; }
    public boolean atualizaEndereco(int p, long e) { if (p<0 || p >= Math.pow(2, profundidadeGlobal)) return false; enderecos[p] = e; return true; }
    public byte[] toByteArray() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      dos.writeByte(profundidadeGlobal);
      int quantidade = (int) Math.pow(2, profundidadeGlobal);
      for (int i = 0; i < quantidade; i++) { dos.writeLong(enderecos[i]); }
      return baos.toByteArray();
    }
    public void fromByteArray(byte[] ba) throws IOException {
      ByteArrayInputStream bais = new ByteArrayInputStream(ba);
      DataInputStream dis = new DataInputStream(bais);
      profundidadeGlobal = dis.readByte();
      int quantidade = (int) Math.pow(2, profundidadeGlobal);
      enderecos = new long[quantidade];
      for (int i = 0; i < quantidade; i++) { enderecos[i] = dis.readLong(); }
    }
    public String toString() {
      String s = "\nPG: " + profundidadeGlobal;
      int quantidade = (int) Math.pow(2, profundidadeGlobal);
      for (int i = 0; i < quantidade; i++) { s += "\n" + i + ": " + enderecos[i]; }
      return s;
    }
    protected long endereço(int p) { if (p<0 || p >= Math.pow(2, profundidadeGlobal)) return -1; return enderecos[p]; }
    protected boolean duplica() {
      if (profundidadeGlobal == 127) return false;
      profundidadeGlobal++;
      int q1 = (int) Math.pow(2, profundidadeGlobal - 1);
      int q2 = (int) Math.pow(2, profundidadeGlobal);
      long[] novosEnderecos = new long[q2];
      for (int i = 0; i < q1; i++) { novosEnderecos[i] = enderecos[i]; novosEnderecos[i+q1] = enderecos[i]; }
      enderecos = novosEnderecos;
      return true;
    }
    protected int hash(int chave) { return Math.abs(chave) % (int) Math.pow(2, profundidadeGlobal); }
    protected int hash2(int chave, int pl) { return Math.abs(chave) % (int) Math.pow(2, pl); }
  }

  public ExtensibleHashTable(Constructor<T> ct, int n, String nd, String nc) throws Exception {
    construtor = ct;
    quantidadeDadosPorCesto = n;
    nomeArquivoDiretorio = nd;
    nomeArquivoCestos = nc;
    arqDiretorio = new RandomAccessFile(nomeArquivoDiretorio, "rw");
    arqCestos = new RandomAccessFile(nomeArquivoCestos, "rw");
    if (arqDiretorio.length() == 0 || arqCestos.length() == 0) {
      diretorio = new Diretorio();
      byte[] bd = diretorio.toByteArray();
      arqDiretorio.write(bd);
      Cesto c = new Cesto(construtor, quantidadeDadosPorCesto);
      bd = c.toByteArray();
      arqCestos.seek(0);
      arqCestos.write(bd);
    }
  }

  public boolean create(T elem) throws Exception {
    byte[] bd = new byte[(int) arqDiretorio.length()];
    arqDiretorio.seek(0);
    arqDiretorio.read(bd);
    diretorio = new Diretorio();
    diretorio.fromByteArray(bd);
    int i = diretorio.hash(elem.hashCode());
    long enderecoCesto = diretorio.endereço(i);
    Cesto c = new Cesto(construtor, quantidadeDadosPorCesto);
    byte[] ba = new byte[c.size()];
    arqCestos.seek(enderecoCesto);
    arqCestos.readFully(ba);
    c.fromByteArray(ba);
    if (c.read(elem.hashCode()) != null) throw new Exception("Elemento já existe");
    if (!c.full()) {
      c.create(elem);
      arqCestos.seek(enderecoCesto);
      arqCestos.write(c.toByteArray());
      return true;
    }
    byte pl = c.profundidadeLocal;
    if (pl >= diretorio.profundidadeGlobal) diretorio.duplica();
    byte pg = diretorio.profundidadeGlobal;
    Cesto c1 = new Cesto(construtor, quantidadeDadosPorCesto, pl + 1);
    arqCestos.seek(enderecoCesto);
    arqCestos.write(c1.toByteArray());
    Cesto c2 = new Cesto(construtor, quantidadeDadosPorCesto, pl + 1);
    long novoEndereco = arqCestos.length();
    arqCestos.seek(novoEndereco);
    arqCestos.write(c2.toByteArray());
    int inicio = diretorio.hash2(elem.hashCode(), c.profundidadeLocal);
    int deslocamento = (int) Math.pow(2, pl);
    int max = (int) Math.pow(2, pg);
    boolean troca = false;
    for (int j = inicio; j < max; j += deslocamento) {
      if (troca) diretorio.atualizaEndereco(j, novoEndereco);
      troca = !troca;
    }
    bd = diretorio.toByteArray();
    arqDiretorio.seek(0);
    arqDiretorio.write(bd);
    for (int j = 0; j < c.quantidade; j++) { create(c.elementos.get(j)); }
    create(elem);
    return true;
  }

  public T read(int chave) throws Exception {
    byte[] bd = new byte[(int) arqDiretorio.length()];
    arqDiretorio.seek(0);
    arqDiretorio.read(bd);
    diretorio = new Diretorio();
    diretorio.fromByteArray(bd);
    int i = diretorio.hash(chave);
    long enderecoCesto = diretorio.endereço(i);
    Cesto c = new Cesto(construtor, quantidadeDadosPorCesto);
    byte[] ba = new byte[c.size()];
    arqCestos.seek(enderecoCesto);
    arqCestos.readFully(ba);
    c.fromByteArray(ba);
    return c.read(chave);
  }

  public boolean update(T elem) throws Exception {
    byte[] bd = new byte[(int) arqDiretorio.length()];
    arqDiretorio.seek(0);
    arqDiretorio.read(bd);
    diretorio = new Diretorio();
    diretorio.fromByteArray(bd);
    int i = diretorio.hash(elem.hashCode());
    long enderecoCesto = diretorio.endereço(i);
    Cesto c = new Cesto(construtor, quantidadeDadosPorCesto);
    byte[] ba = new byte[c.size()];
    arqCestos.seek(enderecoCesto);
    arqCestos.readFully(ba);
    c.fromByteArray(ba);
    if (!c.update(elem)) return false;
    arqCestos.seek(enderecoCesto);
    arqCestos.write(c.toByteArray());
    return true;
  }

  public boolean delete(int chave) throws Exception {
    byte[] bd = new byte[(int) arqDiretorio.length()];
    arqDiretorio.seek(0);
    arqDiretorio.read(bd);
    diretorio = new Diretorio();
    diretorio.fromByteArray(bd);
    int i = diretorio.hash(chave);
    long enderecoCesto = diretorio.endereço(i);
    Cesto c = new Cesto(construtor, quantidadeDadosPorCesto);
    byte[] ba = new byte[c.size()];
    arqCestos.seek(enderecoCesto);
    arqCestos.readFully(ba);
    c.fromByteArray(ba);
    if (!c.delete(chave)) return false;
    arqCestos.seek(enderecoCesto);
    arqCestos.write(c.toByteArray());
    return true;
  }

  public void print() {
    //...
  }
}