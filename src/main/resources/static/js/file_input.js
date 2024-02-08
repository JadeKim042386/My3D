window.addEventListener('load', function () {
    const input = document.getElementById('modelFile');
    const uploadedFile = input.files[0];
    const fileName = document.getElementById('modelFileName').getAttribute('value');
    if (uploadedFile == null && fileName != null) {
        const newFile = new File(["NotUpdated"], fileName);
        const dT = new DataTransfer();
        dT.items.add(newFile);
        input.files = dT.files;
    }
})
