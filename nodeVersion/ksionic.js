#!/usr/bin/env node

var program = require('commander');
var unzip = require('unzip');
const fs = require('fs');
const spawn = require('child_process').spawn;
const util = require('util');
const request = require('request');

var comdOptions = {
    stdio: 'pipe',
    shell: true
  };


const consoleStyles = {
    bright:'\x1b[1m', // Bright
    dim: '\x1b[2m', //Dim
    underscore: '\x1b[4m', //Underscore 
    blink: '\x1b[5m', //Blink 
    reverse: '\x1b[7m', //reverse 
    hidden: '\x1b[8m', //hidden 
};

const consoleColorReset = '\x1b[0m';

const consoleFgClors = {
    black : '\x1b[30m',
    red : '\x1b[31m',
    green : '\x1b[32m',
    yellow : '\x1b[33m',
    blue : '\x1b[34m',
    magenta : '\x1b[35m',
    cyan : '\x1b[36m',
    white : '\x1b[37m',
}

//###Utility functions
function setConsoleColor(color){
    process.stdout.write(color);
}

function fileExists(filePath)
{
    try
    {
        return fs.statSync(filePath).isDirectory();
    }
    catch (err)
    {
        return false;
    }
}

function extractZipFile(zipFile,destination,onSuccess,onFail){
    fs.createReadStream(zipFile)
    .on('error', onFail)
    .pipe(unzip.Extract({ path: destination }))
    .on('error', onFail)
    .on('finish', onSuccess);
}

//####CLI CMD RELATED FUNCTIONS
function changeCliDerectory(path){
    console.log("CLI working directory changed to %s",path);
    comdOptions = {
        cwd: path,
        stdio: 'pipe',
        shell: true
      };
}

function executeCmd(command, args,onSuccess, onFail) {
    if (!args) {
        args = [];
    }
    var comd = spawn(command,args,comdOptions);
    var out = '',err = '';
    
    comd.stdout.on('data', function (data) {
    //console.log('\x1b[36m%s\x1b[0m', 'I am cyan');  //cyan
    // console.log('\x1b[36m%s\x1b[0m', data.toString());
    process.stdout.write(data);
    out += data;
    });
    
    comd.stderr.on('data', function (data) {
    console.log('\x1b[41m%s\x1b[0m',data.toString());
    });
    
    comd.on('error', function (code) {
        console.log('child process exited with code ' + code);
        err += code;
        if(onFail) onFail(err);
    });
    
    comd.on('exit', function (code) {
    console.log('child process exited with code ' + code.toString());
    if (err.length){
        if(onFail) {onFail(err)}
    } else {
        if(onSuccess){onSuccess(out);}
    };
    });
}


//### TEMP FUNCTION TO CONFIGURE MODULE, THIS SHOULD BE A SEPERATE COMPLEX COMPONENT.
function configureModule(moduleName,projectFolderPath,onComplete){
    var isCompleted = false;
    var complete = function(){
        if(!isCompleted){
            isCompleted = true;
            setConsoleColor(consoleColorReset);   
            if(onComplete){
                onComplete();
            }
        }
    };
    setConsoleColor(consoleStyles.dim);
    if(moduleName === 'vlogger'){
      //vlogger is a provider component
      //So init provider in project
      var dldURl = 'https://raw.githubusercontent.com/randikapw/vModuleAdder/252b15efb190a49df3eba57c93ec146e58b9a186/testData/v-logger.zip';
      console.log('Downloading vLogger module:%s', dldURl);
      request(dldURl,function(e,r,b){
          var message = "";
          if(e){
              message += e + "\n"; 
          }
          if (r && r.statusCode !== 200) { 
            message += "with status code: " + r.statusCode + "\n" + r.body;
          }
          if (message.length > 0) {
            setConsoleColor(consoleFgClors.red);
            console.log('Download failed:  %s',message);
            complete();
          }
          
      })      
      .pipe(fs.createWriteStream('temp/vLogger.zip'))
      .on('close', function () {
        console.log('Download completed!');
        var command = 'ionic g provider vLogger';
        console.log('Generate vLogger provider\n> %s',command);
        executeCmd(command,null,function(){
            console.log('Transformng provider src...');
            extractZipFile('temp/vLogger.zip',projectFolderPath + '/src/providers',function(){
                setConsoleColor(consoleFgClors.yellow);
                console.log('vLogger provider configured!');
                complete();
            },function(e){
                setConsoleColor(consoleFgClors.red);
                console.log('vLogger provider not configured properly!\n%s',e.toString());
                complete();
            })
        });
      });
        
      
    } else {
      setConsoleColor(consoleFgClors.red);
      console.log('Cannot find module %s',moduleName);
      complete();
    }
}


//###DEFINED APP COMMANDS
function cmdStart(name, template , options) {
    setConsoleColor(consoleStyles.bright);
    //#### TEMP REMOVING DERECTORY
    //executeCmd('rmdir',[name,'/s','/q']);
    //setConsoleColor(consoleFgClors.green);
    console.log('Action Plan:');
    var step = 1;
    console.log('%d. Create Ionic project with name: %s using template: %s',step++,name,template);
    setConsoleColor(consoleStyles.dim);
    var projectCreteCommand = util.format('ionic start %s %s --cordova --no-git --no-link',name,template);
    console.log('> %s',projectCreteCommand);
    executeCmd(
        projectCreteCommand,
        //  util.format('echo %s-%s',name,template),
        [],
        function(){
            changeCliDerectory('./'+name);
        if(options.setup_modules && options.setup_modules.length){
            var modules = options.setup_modules.split(",");
            //create a temp directory
            var dir = './temp';
            
            if (!fs.existsSync(dir)){
                fs.mkdirSync(dir);
            }
            var i= -1;
            var next = function(){
                if (++i < modules.length) {
                    var cModule = modules[i];
                    if (cModule && cModule.length){
                        setConsoleColor(consoleColorReset);
                        setConsoleColor(consoleStyles.bright);
                        console.log('\n%d. Setup %s module to the project.',step++,modules[i]);
                        configureModule(cModule,'./'+name,next);
                    } else {
                        next();
                    }
                } else {
                    setConsoleColor(consoleFgClors.green);
                    console.log('\nAction completed.');
                    setConsoleColor(consoleColorReset);
                }
            }
            next();
        }
    });

}
// program
//     .version('0.0.1')
//     .command('fname <name>')
//     // .option('-l, --list [list]','list of customers in CSV file')
//     .action(function (name) {
//         console.log('name %s', name);
//         console.log(program.list);
//       });
// //     .parse(process.argv)

// //console.log(program.name);
// // console.log(program.list);
// program
// .version('0.1.0')
// .option('-C, --chdir <path>', 'change the working directory')
// .option('-c, --config <path>', 'set config path. defaults to ./deploy.conf')
// .option('-T, --no-tests', 'ignore test hook');

program
.command('start <name> <template>')
.description('run setup commands for all envs')
.option("-s, --setup_modules [modules]", "Which setup mode to use")
.action(cmdStart);

// program
// .command('exec <cmd>')
// .alias('ex')
// .description('execute the given remote cmd')
// .option("-e, --exec_mode <mode>", "Which exec mode to use")
// .action(function(cmd, options){
//   console.log('exec "%s" using %s mode', cmd, options.exec_mode);
// }).on('--help', function() {
//   console.log('  Examples:');
//   console.log();
//   console.log('    $ deploy exec sequential');
//   console.log('    $ deploy exec async');
//   console.log();
// });

// program
// .command('*')
// .action(function(env){
//   console.log('deploying "%s"', env);
// });

program.parse(process.argv);

//################



//console.log(process.argv);

