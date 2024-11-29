package org.davidCMs.engine.render.model;

import org.davidCMs.engine.render.renderer.Material;
import org.davidCMs.engine.render.renderer.oglobjects.EBO;
import org.davidCMs.engine.render.renderer.oglobjects.VAO;
import org.davidCMs.engine.render.renderer.oglobjects.VBO;

public class Mesh {
    private VAO vao;
    private Material material;

    public static final Material defaultMaterial = new Material();

    public Mesh() {
        this.vao = new VAO(new VBO(), new EBO());
        material = defaultMaterial;
    }
    public Mesh(VAO vao) {
        this.vao = vao;
        material = defaultMaterial;
    }
    public Mesh(VAO vao, Material material) {
        this.vao = vao;
        this.material = material;
    }

    public VAO getVao() {
        return vao;
    }

    public void setVao(VAO vao) {
        this.vao = vao;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
