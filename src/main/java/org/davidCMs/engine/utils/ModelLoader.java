package org.davidCMs.engine.utils;

import org.davidCMs.engine.render.model.Mesh;
import org.davidCMs.engine.render.renderer.oglobjects.EBO;
import org.davidCMs.engine.render.renderer.oglobjects.GLDrawType;
import org.davidCMs.engine.render.renderer.oglobjects.VAO;
import org.davidCMs.engine.render.renderer.oglobjects.VBO;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

public class ModelLoader {

    public static Mesh loadMesh(AIMesh mesh, GLDrawType type) {
        AIVector3D.Buffer positions = mesh.mVertices();
        AIVector3D.Buffer normals = mesh.mNormals();
        AIVector3D.Buffer texCoords = mesh.mTextureCoords(0);
        AIColor4D.Buffer colors = mesh.mColors(0);
        AIFace.Buffer faces = mesh.mFaces();

        int vertexCount = positions.limit();
        int stride = 3 + 3 + 2 + 4;
        FloatBuffer vertexBuffer = FloatBuffer.allocate(vertexCount * stride);

        for (int i = 0; i < vertexCount; i++) {
            AIVector3D pos = positions.get(i);
            AIVector3D normal = normals.get(i);
            AIVector3D texCoord = texCoords.get(i);

            vertexBuffer.put(pos.x()).put(pos.y()).put(pos.z());
            vertexBuffer.put(normal.x()).put(normal.y()).put(normal.z());
            vertexBuffer.put(texCoord.x()).put(texCoord.y());
            vertexBuffer.put(1).put(1).put(1).put(1);
        }
        vertexBuffer.flip();

        IntBuffer indexBuffer = IntBuffer.allocate(faces.remaining() * 3);
        while (faces.hasRemaining()) {
            AIFace face = faces.get();
            if (face.mNumIndices() != 3) {
                throw new RuntimeException("Non-triangular face found.");
            }
            for (int i = 0; i < 3; i++) {
                indexBuffer.put(face.mIndices().get(i));
            }
        }
        indexBuffer.flip();

        float[] vert = new float[vertexBuffer.remaining()];
        vertexBuffer.get(vert);
        VBO vbo = new VBO(vert, type);

        int[] indices = new int[indexBuffer.remaining()];
        indexBuffer.get(indices);
        EBO ebo = new EBO(indices, type);

        VAO vao = new VAO(vbo, ebo);

        return new Mesh(vao);
    }
}
