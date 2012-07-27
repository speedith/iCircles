package icircles.gui;

/*
 * @author Aidan Delaney <aidan@phoric.eu>
 * Copyright (c) 2012
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the iCircles Project.
 */

import icircles.abstractDescription.AbstractDescription;
import icircles.concreteDiagram.DiagramCreator;
import icircles.util.*;

import org.apache.commons.cli.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;


/**
 * Basic command line interface to the iCircles algorithm.  Requries a JSON
 * description of an AbstractDescription and will print an SVG file to stdout.
 * Return value of 0 indicates success
 *                 1 indicates some input parsing error
 *                 2 indicates that the passed in diagram cannot be drawn
 *
 * Currently the input data is not validated.  The Garbage In - Garbage Out
 * principle applies.  TODO: validate user input.
 */
public class CommandLineUI {

    private AbstractDescription abstractDescription;
    private int canvasSize;
    private Options options;

    private boolean parseOptions(String [] argv) {
        // Create the options, using long options in the BSD/Java style
        // so -abstract-description is a required command line option
        Option aDescription = OptionBuilder.withArgName( "file" )
            .hasArg()
            .isRequired()
            .withDescription(  "A file containing an Abstract Description in JSON format" )
            .create( "abstractdescription" );

        Option aSize = OptionBuilder.withArgName( "size" )
            .hasArg()
            .withDescription(  "The size, in pixels, of the canvas (default 200)" )
            .create( "size" );

        Option help = new Option ("help", "Prints this help message");

        options = new Options();
        options.addOption(aDescription);
        options.addOption(aSize);
        options.addOption(help);

        // Parse the command line
        CommandLineParser parser = new GnuParser();
        CommandLine line;
        try {
            // parse the command line arguments
            line = parser.parse( options, argv );
        }
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            return false;
        }

        if(line.hasOption("help")) {
            return false;
        }

        // Ensure we have an abstractdescription
        if( line.hasOption( "abstractdescription" ) ) {
            String jsonFile = line.getOptionValue( "abstractdescription" );

            // Pull in the AbstractDescription from JSON file
            ObjectMapper mapper = new ObjectMapper();
            try {
                this.abstractDescription = mapper.readValue(new File(jsonFile), AbstractDescription.class);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return false;
            }
        } else {
            return false;
        }

        if( line.hasOption( "size" ) ) {
            String size = line.getOptionValue( "size" );
            // TODO: check exceptions properly
            try {
                this.canvasSize = Integer.parseInt(size);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                return false;
            }
        } else {
            this.canvasSize = 200;
        }

        return true;
    }

    public void run(String [] argv) {
        if (!parseOptions(argv)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "CommandLineUI", options );
            System.exit(1);
        }

        DiagramCreator dc       = new DiagramCreator(abstractDescription);

        try {
            CirclesSVGGenerator csg = new CirclesSVGGenerator(dc.createDiagram(canvasSize));

            System.out.println(csg.toString());
        } catch (CannotDrawException cde) {
            cde.printStackTrace();
            System.exit(2);
        }
    }

    public static void main(String [] argv) {
        CommandLineUI clu = new CommandLineUI();
        clu.run(argv);
    }
}
