window.addEventListener('load', function () {
    const input = document.getElementById('inputGroupFile');
    const uploadedFile = input.files[0];
    const fileName = document.getElementById('fileName').getAttribute('value');
    if (uploadedFile == null && fileName != null) {
        const newFile = new File(["NotUpdated"], fileName);
        console.log(newFile);
        const dT = new DataTransfer();
        dT.items.add(newFile);
        console.log(dT);
        input.files = dT.files;
    }
})