package domain.filemanager.mock;

import domain.filemanager.api.entity.File;
import domain.filemanager.api.entity.Permission;
import domain.filemanager.spi.FileRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MockInMemoryFile implements FileRepository {

    private final Map<String, MockFile> filesInMemory = new HashMap<>();

    public Map<String, MockFile> getAllfiles() {
        return filesInMemory;
    }

    public Optional<MockFile> findByName(String fileName) {
        return filesInMemory.values().stream()
            .filter(mockFile -> hasFileName(mockFile, fileName))
            .findFirst();
    }

    private boolean hasFileName(MockFile file, String fileName) {
        return fileName.equals(file.getName());
    }

    @Override
    public File findFileById(String fileId) {
        return filesInMemory.get(fileId);
    }

    @Override
    public List<File> findFilesByUserId(String ownerId) {
        return filesInMemory.values()
                .stream()
                .filter(file -> ownerId.equals(file.getOwnerId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<File> findFilesBySharedUser(String userId) {
        return filesInMemory.values()
                .stream()
                .filter(f -> isSharedToUser(f, userId))
                .collect(Collectors.toList());
    }

    private boolean isSharedToUser(File file, String userId) {
        return file.getSharedUsersIdWithPermission()
                .keySet()
                .contains(userId);
    }

    @Override
    public File addFile(String name, byte[] data, String ownerId) {
        MockFile fileToSave = new MockFile(autoGeneratedId(), name, data, ownerId);
        filesInMemory.put(fileToSave.getId(), fileToSave);
        return fileToSave;
    }

    @Override
    public void shareFile(String fileId, Map<String, Permission> usersIdToShareWithPermission) {
        MockFile fileToAddShareUsers = filesInMemory.get(fileId);
        fileToAddShareUsers.setSharedUsersIdWithPermission(usersIdToShareWithPermission);
    }

    @Override
    public void deleteFile(String name) {
        filesInMemory.remove(name);
    }

    private String autoGeneratedId() {
        return String.valueOf(filesInMemory.size());
    }
}
