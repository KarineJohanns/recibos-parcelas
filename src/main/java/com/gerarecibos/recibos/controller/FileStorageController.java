package com.gerarecibos.recibos.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.gerarecibos.recibos.FileStorageProperties;
import com.gerarecibos.recibos.model.Recibo;
import com.gerarecibos.recibos.repository.ReciboRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/files")
public class FileStorageController {

    private final Path fileStorageLocation;

    private final ReciboRepository reciboRepository;

    // Use @Value para pegar o caminho da propriedade definida no application.properties
    @Autowired
    public FileStorageController(@Value("${file.upload-dir}") String storageLocation, ReciboRepository reciboRepository) {
        // Inicializar o caminho de onde os arquivos serão salvos
        this.fileStorageLocation = Paths.get(storageLocation).toAbsolutePath().normalize();

        // Criar os diretórios, se ainda não existirem
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível criar o diretório para armazenar os arquivos.", ex);
        }

        this.reciboRepository = reciboRepository;  // Injeção do repository
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("parcelaId") Long parcelaId) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Local onde o arquivo será salvo
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            file.transferTo(targetLocation);

            // Gerar a URI de download do arquivo
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/")
                    .path(fileName)
                    .toUriString();

            // Recuperar o recibo associado à parcela com ID fornecido
            Recibo recibo = reciboRepository.findByParcelaId(parcelaId)  // Buscar pelo reciboId
                    .orElseThrow(() -> new RuntimeException("Recibo não encontrado para a parcela com ID: " + parcelaId));

            // Atualizar o campo `uri` do recibo
            recibo.setUri(fileDownloadUri);
            reciboRepository.save(recibo);  // Salvar o recibo atualizado com a URI

            return ResponseEntity.ok("File uploaded successfully. Download link: " + fileDownloadUri);
        } catch (IOException ex) {
            return ResponseEntity.badRequest().body("File upload failed.");
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName,
                                                 HttpServletRequest request) throws IOException {
        Path filePath = fileStorageLocation.resolve(fileName).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());

            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() throws IOException {
        List<String> fileNames = Files.list(fileStorageLocation)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());

        return ResponseEntity.ok(fileNames);
    }
}