
// ensure no aliases are used in exported model YAML file
File sevices1ModelFile = new File( basedir, "environments/target/configuration/prod/services-1/model.yaml" );
assert sevices1ModelFile.exists();
String sevices1Model = sevices1ModelFile.getText("utf-8");
assert !sevices1Model.contains("&id001")
assert !sevices1Model.contains("*id001")


return true;
