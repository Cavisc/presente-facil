package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import util.Encryption;

public class User implements Generic  {
    private int id;
    private String name;
    private String email;
    private String hashPassword;
    private String secretQuestion;
    private String secretAnswer;

    public User() {
        this.id = -1;
        this.name = "";
        this.email = "";
        this.hashPassword = "";
        this.secretQuestion = "";
        this.secretAnswer = "";
    }

    public User(int id, String name, String email, String hashPassword, String secretQuestion, String secretAnswer) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hashPassword = hashPassword;
        this.secretQuestion = secretQuestion;
        this.secretAnswer = secretAnswer;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public String getHashPassword() {
        return this.hashPassword;
    }

    public void setSecretQuestion(String secretQuestion) {
        this.secretQuestion = secretQuestion;
    }

    public String getSecretQuestion() {
        return this.secretQuestion;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }

    public String getSecretAnswer() {
        return this.secretAnswer;
    }   

    public boolean validatePassword(String password) {
        return Encryption.validatePassword(password, this.hashPassword);
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.name);
        dos.writeUTF(this.email);
        dos.writeUTF(this.hashPassword);
        dos.writeUTF(this.secretQuestion);
        dos.writeUTF(this.secretAnswer);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.name = dis.readUTF();
        this.email = dis.readUTF();
        this.hashPassword = dis.readUTF();
        this.secretQuestion = dis.readUTF();
        this.secretAnswer = dis.readUTF();
    }
}
