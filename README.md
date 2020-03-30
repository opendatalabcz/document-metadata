# Elasticsearch Metadata Extractor plugin
[<img src="https://logodix.com/logo/1687804.png" height="100">](https://www.elastic.co/)[<img src="https://logodix.com/logo/1024526.png" height="100">](https://www.apache.org/)



Elasticsearch metadata extractor plugin is used to extract metadata from file (local or from server) and then index them into chosen index.

  - Easy to use with single endpoint
  - Using powerful and stable Apache libraries for extraction
  - Written in JAVA

# Installation
  - Download **metadata-extractor-x.y.z.zip** (x.y.z represents version of elasticsearch,  version used in this example: **7.5.0**) from [repository](https://github.com/opendatalabcz/document-metadata/tree/master/metadata-extractor/releases)
  ```sh
$ wget "https://github.com/opendatalabcz/document-metadata/raw/master
```
  - Download and extract [elasticsearch](https://www.elastic.co/downloads/past-releases#elasticsearch) with the same version as metadata-extractor plugin
   ```sh
$ wget "https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.5.0-linux-x86_64.tar.gz"
$ tar -xvf ./elasticsearch-7.5.0-linux-x86_64.tar.gz
```
  - Install metadata-extractor plugin (answer **y** to plugin permission) 
 ```sh 
$ ./elasticsearch-7.5.0/bin/elasticsearch-plugin install file://$PWD/metadata-extractor-7.5.0.zip
```
- Start elasticsearch with installed metadata-extractor plugin
 ```sh 
$ ./elasticsearch-7.5.0/bin/elasticsearch
```
**TIPS:**
  - Always keep **same version** of plugin (zip file) and elasticsearch
  - You can check installed plugin description with command:
  ```sh 
$ ./elasticsearch-7.5.0/bin/elasticsearch-plugin list --verbose
```
  - You can remove installed plugin with command:
  ```sh 
$ ./elasticsearch-7.5.0/bin/elasticsearch-plugin remove metadata-extractor
```

# Tutorial
Simple request extracting metadata from pdf file: **/home/extractor/pdftest.pdf** and indexing it to index: **test**



> The overriding design goal for Markdown's
> formatting syntax is to make it as readable
> as possible. The idea is that a
> Markdown-formatted document should be
> publishable as-is, as plain text, without
> looking like it's been marked up with tags
> or formatting instructions.

This text you see here is *actually* written in Markdown! To get a feel for Markdown's syntax, type some text into the left window and watch the results in the right.

### Tech

Dillinger uses a number of open source projects to work properly:

* [AngularJS] - HTML enhanced for web apps!
* [Ace Editor] - awesome web-based text editor
* [markdown-it] - Markdown parser done right. Fast and easy to extend.
* [Twitter Bootstrap] - great UI boilerplate for modern web apps
* [node.js] - evented I/O for the backend
* [Express] - fast node.js network app framework [@tjholowaychuk]
* [Gulp] - the streaming build system
* [Breakdance](https://breakdance.github.io/breakdance/) - HTML to Markdown converter
* [jQuery] - duh

And of course Dillinger itself is open source with a [public repository][dill]
 on GitHub.

### Installation

Dillinger requires [Node.js](https://nodejs.org/) v4+ to run.

Install the dependencies and devDependencies and start the server.

```sh
$ cd dillinger
$ npm install -d
$ node app
```

For production environments...

```sh
$ npm install --production
$ NODE_ENV=production node app
```

### Plugins

Dillinger is currently extended with the following plugins. Instructions on how to use them in your own application are linked below.

| Plugin | README |
| ------ | ------ |
| Dropbox | [plugins/dropbox/README.md][PlDb] |
| GitHub | [plugins/github/README.md][PlGh] |
| Google Drive | [plugins/googledrive/README.md][PlGd] |
| OneDrive | [plugins/onedrive/README.md][PlOd] |
| Medium | [plugins/medium/README.md][PlMe] |
| Google Analytics | [plugins/googleanalytics/README.md][PlGa] |


### Development

Want to contribute? Great!

Dillinger uses Gulp + Webpack for fast developing.
Make a change in your file and instantaneously see your updates!

Open your favorite Terminal and run these commands.

First Tab:
```sh
$ node app
```

