package com.app.tensquare.utils

class MathsConvert {
    companion object{

        var JS_FILES = "<script>\n" +
                "MathJax = {" +
                "  tex: {" +
                "    inlineMath: [['$', '$'], ['\\(', '\\)']]\n" +
                "  }" +
                "};" +
                "</script>" +"<style type=\"text/css\">\n" +
                "html, body {" +
                "margin: 0px;" +
                "padding: 0px;" +
                "}" +
                "</style>"+
                "<script id=\"MathJax-script\" async src=\"https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-chtml.js\"></script>\n" +
                "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/katex@0.11.1/dist/katex.min.css\" crossorigin=\"anonymous\">" +"<style>img{display: inline;height: auto;max-width: 100%;}</style>"


        var JQMath="<link rel=\"stylesheet\" href="+"https://mathscribe.com/mathscribe/jqmath-0.4.3.css>" +
                "\n" +
                "<script src="+"https://mathscribe.com/mathscribe/jquery-1.4.3.min.js></script>\n" +
                "<script src="+"https://mathscribe.com//mathscribe/jqmath-etc-0.4.6.min.js charset=\"utf-8\"></script>" +
                "<script src="+"https://mathscribe.com//mathscribe/jscurry-0.4.5.js charset=\"utf-8\"></script>" +
                "<script src="+"https://mathscribe.com//mathscribe/jqmath-0.4.6.min.js charset=\"utf-8\"></script>" +
                "<style>img{" +"max-width: 100%; " +
                "width:auto; height: " +
                "auto"+

                "                }</style>"
    }


}